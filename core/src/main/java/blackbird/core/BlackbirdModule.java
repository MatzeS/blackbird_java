package blackbird.core;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class BlackbirdModule {
    @Provides
    @Singleton
    static Blackbird provideBlackbird() {
        return new Blackbird();
    }
}
