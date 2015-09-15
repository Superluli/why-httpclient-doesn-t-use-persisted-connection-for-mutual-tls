package xx;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class Client {

	public static ExecutorService service = Executors.newFixedThreadPool(1000);

	public static void main(String[] args) throws Exception {

		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime",
				"true");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient",
				"debug");

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setDefaultMaxPerRoute(1);

		CloseableHttpClient client = HttpClients.custom()
				.setConnectionManager(cm).build();

		for (int i = 0; i < 10; i++) {

			service.submit(new CallingTask(client));
		}
	}

	static class CallingTask implements Callable<Void> {

		CloseableHttpClient client;

		public CallingTask(CloseableHttpClient client) {
			this.client = client;
		}

		@Override
		public Void call() throws Exception {

			HttpGet httpGet = new HttpGet("http://localhost:9999/resource");
			CloseableHttpResponse response = client.execute(httpGet);
			Thread.sleep(100);
			System.err.println(Thread.currentThread().getName() + " : "
					+ EntityUtils.toString(response.getEntity()));
			
			return null;
		}
	}
}
