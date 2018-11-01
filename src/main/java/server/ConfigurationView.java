package server;

import java.util.concurrent.atomic.AtomicInteger;

public class ConfigurationView {


    private static AtomicInteger integer = new AtomicInteger(1);

    public static ConfigurationView create() {
        ConfigurationView configurationView = new ConfigurationView();
        configurationView.setKey("atomicInteger");
        configurationView.setValue("" + integer.getAndIncrement());
        return configurationView;

    }

    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
