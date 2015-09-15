package xx;

import java.io.File;
import java.io.IOException;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
    	
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        tomcat.addAdditionalTomcatConnectors(createSslConnector());
        return tomcat;
    }

    private Connector createSslConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        try {
            File keystore = new ClassPathResource("ca.p12").getFile();
            File truststore = new ClassPathResource("ca.p12").getFile();
            connector.setScheme("https");
            connector.setSecure(true);
            connector.setPort(8443);
            
            protocol.setSSLEnabled(true);
            protocol.setClientAuth("true");
            protocol.setSslProtocol("TLS");
            protocol.setKeystoreFile(keystore.getAbsolutePath());
            protocol.setKeystorePass("19880131");
            protocol.setKeystoreType("pkcs12");
            
            protocol.setTruststoreFile(truststore.getAbsolutePath());
            protocol.setTruststorePass("19880131");
            protocol.setTruststoreType("pkcs12");
            return connector;
        }
        catch (IOException ex) {
            throw new IllegalStateException("can't access keystore: [" + "keystore"
                    + "] or truststore: [" + "keystore" + "]", ex);
        }
    }
}
