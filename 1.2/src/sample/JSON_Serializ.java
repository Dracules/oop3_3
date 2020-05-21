package sample;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Scanner;

public class JSON_Serializ extends Serializ_Deserializ {

    public void Save(ArrayList<learner> arr,int EncodeType, pluginLoad pluginLoader) {
        try {
            String json;
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(learner.class, new InterfaceAdapter());
            Gson gson = builder.create();
            json = gson.toJson(arr,new TypeToken<java.util.List<learner>>() {}.getType());

            String encode=null;

            File serialFiles =  new File("serialFiles");
            File[] jars = serialFiles.listFiles(file -> file.isFile() && file.getName().startsWith("JSON"));
            if (jars.length!=0)
                jars[0].delete();
            FileWriter nFile = null;
            if (EncodeType == 0)
            {
                nFile = new FileWriter("serialFiles/JSON");
                encode =json;
            }
            else
            {
                nFile = new FileWriter( "serialFiles/JSON."+ pluginLoader.getPluginsNames()[EncodeType-1]);
                encode =pluginLoader.encoding(json,EncodeType-1);
            }
            nFile.write(encode);

            nFile.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<learner> Load(pluginLoad pluginLoader) {
        ArrayList<learner> arr = new ArrayList<learner>();
        try {
            File serialFiles =  new File("serialFiles");
            File[] jars = serialFiles.listFiles(file -> file.isFile() && file.getName().startsWith("JSON"));
            FileReader nFile = new FileReader("serialFiles/"+jars[0].getName());
            Scanner scan = new Scanner(nFile);

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(learner.class, new InterfaceAdapter());
            Gson gson = builder.create();
            String decode;
            String extension = jars[0].getName().substring(jars[0].getName().lastIndexOf('.')+1);
            String[] extensions = pluginLoader.getPluginsNames();
            int pluginIndex=0;
            decode = scan.nextLine();
            for (String name : extensions)
            {
                if (name.equals(extension))
                {
                    decode = pluginLoader.decoding(decode,pluginIndex);
                    break;
                }
                pluginIndex++;
            }
            arr.addAll(gson.fromJson(decode,new TypeToken<java.util.List<learner>>() {}.getType()));
            nFile.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return arr;
    }

    public class InterfaceAdapter implements JsonSerializer, JsonDeserializer {

        private static final String CLASSNAME = "CLASSNAME";
        private static final String DATA = "DATA";

        public Object deserialize(JsonElement jsonElement, Type type,
                                JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
            String className = prim.getAsString();
            Class klass = getObjectClass(className);
            return jsonDeserializationContext.deserialize(jsonObject.get(DATA), klass);
        }
        public JsonElement serialize(Object jsonElement, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(CLASSNAME, jsonElement.getClass().getName());
            jsonObject.add(DATA, jsonSerializationContext.serialize(jsonElement));
            return jsonObject;
        }
        /****** Helper method to get the className of the object to be deserialized *****/
        public Class getObjectClass(String className) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new JsonParseException(e.getMessage());
            }
        }
    }
}

