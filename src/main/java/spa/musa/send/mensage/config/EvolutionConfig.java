package spa.musa.send.mensage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configurações da Evolution API
 */
@ConfigurationProperties(prefix = "evolution")
public class EvolutionConfig {

    private Api api = new Api();
    private Instance instance = new Instance();

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public static class Api {
        private String url;
        private String key;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class Instance {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
