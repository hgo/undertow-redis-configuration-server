package server;

import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class DB {

    private final Jdbi jdbi;

    public DB(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public List<ConfigurationView> readConfiguration() {
        List<ConfigurationView> list = jdbi.withHandle(handle ->
                handle.createQuery("select * from configuration")
                        .mapToBean(ConfigurationView.class)
                        .list()
        );
        list.add(0, ConfigurationView.create());
        return list;
    }
}
