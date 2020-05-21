package sample;

import java.util.ArrayList;

abstract class Serializ_Deserializ {
    abstract public void Save(ArrayList<learner> arr,int EncodeType, pluginLoad pluginLoader);
    abstract public ArrayList<learner> Load(pluginLoad pluginLoader);
}
