package id.indosw.xlstojson;

import android.app.Application;

public class IndoSW extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory","com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
    }
}
