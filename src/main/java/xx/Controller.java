package xx;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class Controller {

	CloseableHttpClient httpclient;

	HttpGet httpGet;

	RestTemplate template;

	@PostConstruct
	public void init() throws Exception {

		KeyStore myTrustStore = KeyStore.getInstance("pkcs12");
		myTrustStore.load(new FileInputStream("src/main/resources/ca.p12"), "19880131".toCharArray());
		
//		KeyStore myKeyStore = KeyStore.getInstance("pkcs12");
//		myKeyStore.load(new FileInputStream("src/main/resources/ca.p12"), "19880131".toCharArray());
		
		SSLContext sslContext = SSLContexts.custom()
				.loadTrustMaterial(myTrustStore).loadKeyMaterial(myTrustStore, "19880131".toCharArray()).build();

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslContext, new String[] { "TLSv1.2" }, null,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		
		Registry<ConnectionSocketFactory> r = RegistryBuilder
				.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory
						.getSocketFactory())
				.register("https", sslsf).build();

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
				r);
		cm.setDefaultMaxPerRoute(3);
		cm.setMaxTotal(3);
		httpclient = HttpClients.custom().setConnectionManager(cm).build();

		template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(
				httpclient));
	}

	@RequestMapping("/resource")
	ResponseEntity<String> resource() throws Exception {
		return ResponseEntity.ok().header("connection", "keep-alive")
				.body("Hello World!");
	}

	@RequestMapping("/send")
	String send() throws Exception {

		EntityUtils.consume(httpclient.execute(
				new HttpGet("https://localhost:8443")).getEntity());

		return "done";
	}

	@RequestMapping("/rest")
	String rest() throws Exception {

		return template.getForEntity("http://localhost:9999/resource",
				String.class).getBody();
	}
}
