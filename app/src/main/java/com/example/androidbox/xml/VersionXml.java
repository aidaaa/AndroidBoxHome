package com.example.androidbox.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class VersionXml
{
    public Observable<ArrayList<String>> getObservableVersion()
    {
        return Observable.create(new ObservableOnSubscribe<ArrayList<String>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<String>> emitter) throws Exception {
                ArrayList<String> list=getAppVesrion();
                if (list!=null && !emitter.isDisposed())
                {
                    emitter.onNext(list);
                    emitter.onComplete();
                }
                else
                {
                    ArrayList<String> listE=new ArrayList<>();
                    String error="error";
                    listE.add(error);
                    emitter.onNext(listE);

                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public static ArrayList<String> getAppVesrion()
    {
        try {
            ArrayList<String> file = new ArrayList<>();
            String url;
            String version;

            InputStream inputStream = new URL("http://192.168.0.101:8090/android.xml").openStream();

            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder db=dbf.newDocumentBuilder();
            Document d=db.parse(inputStream);
             Element element=d.getDocumentElement();
             element.normalize();

             NodeList nodeList=d.getElementsByTagName("app");

            Node node=nodeList.item(0);
            Element element1= (Element) node;
            url=getValue("url",element1);
            version=getValue("version",element1);

            file.add(url);
            file.add(version);
            return file;

        }
        catch (Exception e)
        {
            e.getMessage();
            return null;
        }
    }

    private static String getValue(String tag, Element element)
    {
        NodeList nodeList=element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node=nodeList.item(0);
        if (node==null)
        {
            return "";
        }
        else {
            String a = node.getNodeValue();
            return node.getNodeValue();
        }
    }
}
