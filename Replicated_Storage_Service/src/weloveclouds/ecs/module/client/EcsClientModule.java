package weloveclouds.ecs.module.client;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.io.InputStream;

import weloveclouds.ecs.module.EcsModule;

/**
 * Created by Benoit on 2016-12-03.
 */
public class EcsClientModule extends AbstractModule {
    @Provides
    public InputStream getClientCLIInputStream(){
        return System.in;
    }


    @Override
    protected void configure() {
        install(new EcsModule());
    }
}
