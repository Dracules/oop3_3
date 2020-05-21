package sample;

import annotations.HierarchyAnnotation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Text_Serializ extends Serializ_Deserializ {

    @Override
    public void Save(ArrayList<learner> arr,int EncodeType, pluginLoad pluginLoader) {
        String encodingString="";
        try {
            File serialFiles =  new File("serialFiles");
            File[] jars = serialFiles.listFiles(file -> file.isFile() && file.getName().startsWith("Text"));
            if (jars.length==1)
                jars[0].delete();
            for (learner learn : arr) {
                Class LeanerClass = learn.getClass();
                Class LeanerSuperClass = LeanerClass.getSuperclass();

                encodingString = encodingString.concat("{" + LeanerClass.getName() + "}");

                if (LeanerSuperClass != learner.class){
                    encodingString = SaveFields(learn, LeanerSuperClass.getSuperclass(),encodingString);
                }
                encodingString = SaveFields(learn, LeanerSuperClass,encodingString);
                encodingString = SaveFields(learn, LeanerClass,encodingString);
                encodingString = encodingString.concat("\n");
            }
            String encode="";
            FileWriter nFile=null;
            if (EncodeType == 0)
            {
                nFile = new FileWriter("serialFiles/Text");
                encode =encodingString;
            }
            else
            {
                nFile = new FileWriter( "serialFiles/Text."+ pluginLoader.getPluginsNames()[EncodeType-1]);
                encode =pluginLoader.encoding(encodingString,EncodeType-1);
            }
            nFile.write(encode);
            nFile.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String SaveFields (Object ob,Class cl, String string) {
        try {
            Field[] ObFields = cl.getDeclaredFields();
            for (Field field : ObFields)
            {
                if (field.getAnnotation(HierarchyAnnotation.class).Type() == scientific.class)
                {
                    scientific Scientif = (scientific) field.get(ob);
                    Field[] ScintificFields = scientific.class.getDeclaredFields();
                    for (Field ScintField : ScintificFields)
                    {
                        string = string.concat("{" + ScintField.get(Scientif).toString() + "}");
                    }
                }
                else {
                    string = string.concat("{" + field.get(ob).toString() + "}");
                }
            }
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return string;
    }


    @Override
    public ArrayList<learner> Load(pluginLoad pluginLoader){
        ArrayList<learner> arr = new ArrayList<learner>();
        try {
            File serialFiles =  new File("serialFiles");
            File[] jars = serialFiles.listFiles(file -> file.isFile() && file.getName().startsWith("Text"));
            FileReader nFile = new FileReader("serialFiles/"+jars[0].getName());
            Scanner scan = new Scanner(nFile);
            String decodingText="";
            while (scan.hasNextLine())
                decodingText= decodingText + scan.nextLine()+'\n';
            decodingText = decodingText.substring(0,decodingText.length()-1);
            scan.close();
            nFile.close();

            String decode="";
            String extension = jars[0].getName().substring(jars[0].getName().lastIndexOf('.')+1);
            String[] extensions = pluginLoader.getPluginsNames();
            int pluginIndex=0;
            decode = decodingText;
            for (String name : extensions)
            {
                if (name.equals(extension))
                {
                    decode = pluginLoader.decoding(decode,pluginIndex);
                    break;
                }
                pluginIndex++;
            }
            StringTokenizer decodeToken = new StringTokenizer(decode, "\n");
            while (decodeToken.hasMoreTokens())
            {
                String str = decodeToken.nextToken();
                StringTokenizer tokenizer = new StringTokenizer(str, "{}");

                Class LeanerClass = Class.forName(tokenizer.nextToken());
                Object learner = LeanerClass.newInstance();

                Class LeanerSuperClass = LeanerClass.getSuperclass();
                if (LeanerSuperClass != learner.class)
                {
                    LoadFields(learner,LeanerSuperClass.getSuperclass() , tokenizer);
                }
                LoadFields(learner,LeanerSuperClass, tokenizer);
                LoadFields(learner,LeanerClass, tokenizer);

                arr.add((learner) learner);
            }
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException |IOException e)
        {
            e.printStackTrace();
        }
        return arr;
    }


    private void LoadFields (Object ob,Class cl, StringTokenizer tokenizer) {
        try {
            Field[] ObFields = cl.getDeclaredFields();
            for (Field field : ObFields) {
                if (field.getAnnotation(HierarchyAnnotation.class).Type() == scientific.class)
                {
                    scientific Scientif = new scientific();
                    Field[] ScintificFields = scientific.class.getDeclaredFields();
                    for (Field ScintField : ScintificFields)
                    {
                        ScintField.set(Scientif, tokenizer.nextToken());
                    }
                    field.set(ob, Scientif);
                }
                else {
                    if (field.getAnnotation(HierarchyAnnotation.class).Type() == int.class) {
                        field.set(ob, Integer.parseInt(tokenizer.nextToken()));
                    } else {
                        field.set(ob, tokenizer.nextToken());
                    }
                }
            }
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }


}
