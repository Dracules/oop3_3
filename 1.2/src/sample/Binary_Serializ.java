package sample;

import java.io.*;
import java.util.ArrayList;

public class Binary_Serializ extends Serializ_Deserializ {
    public void Save(ArrayList<learner> arr,int EncodeType, pluginLoad pluginLoader) {
        try {
            File serialFiles =  new File("serialFiles");
            File[] jars = serialFiles.listFiles(file -> file.isFile() && file.getName().startsWith("Binary"));
            if (jars.length>=1){
                jars[0].delete();
            }
            String Filename = "";
            if (EncodeType == 0)
            {
                Filename = "serialFiles/Binary";
            }
            else
            {
                Filename = "serialFiles/Binary."+ pluginLoader.getPluginsNames()[EncodeType-1];
            }

            FileOutputStream fos = new FileOutputStream(Filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeInt(arr.size());
            for (int i = 0; i < arr.size(); i++) {
                oos.writeObject(arr.get(i));
            }
            oos.flush();
            oos.close();

            FileInputStream fin = new FileInputStream(Filename);
            byte[]buffer=new byte[ fin.available()];
            fin.read(buffer, 0, fin.available());
            byte[]buffer2=null;
            if (EncodeType==0)
            {
                buffer2 =buffer;
            }
            else
            {
                buffer2 =pluginLoader.encoding(buffer,EncodeType-1);
            }
            fin.close();

            FileOutputStream fos2 = new FileOutputStream(Filename);
            fos2.write(buffer2);
            fos2.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<learner> Load(pluginLoad pluginLoader){
        ArrayList<learner> arr = new ArrayList<learner>();
        try {
            File serialFiles =  new File("serialFiles");
            File[] jars = serialFiles.listFiles(file -> file.isFile() && file.getName().startsWith("Binary"));

            FileInputStream fin = new FileInputStream("serialFiles/"+jars[0].getName());
            byte[]buffer=new byte[ fin.available()];
            fin.read(buffer, 0, fin.available());
            byte[]buffer2;
            String extension = jars[0].getName().substring(jars[0].getName().lastIndexOf('.')+1);
            String[] extensions = pluginLoader.getPluginsNames();
            int pluginIndex=0;
            buffer2 = buffer;
            for (String name : extensions)
            {
                if (name.equals(extension))
                {
                    buffer2 = pluginLoader.decoding(buffer,pluginIndex);
                    break;
                }
                pluginIndex++;
            }
            fin.close();

            FileOutputStream fos2 = new FileOutputStream("serialFiles/processing");
            fos2.write(buffer2);
            fos2.close();

            FileInputStream fis = new FileInputStream("serialFiles/processing");
            ObjectInputStream ois = new ObjectInputStream(fis);
            int N =ois.readInt();
            for (int i=0;i<N;i++) {
                arr.add(i, (learner) ois.readObject());
            }
            ois.close();
            fis.close();
            new File("serialFiles/processing").delete();
        }
        catch (ClassNotFoundException | IOException e)
        {
            e.printStackTrace();
        }
        return arr;
    }
}
