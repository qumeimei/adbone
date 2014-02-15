package adbone;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.PriorityQueue;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class BingSearch {
	private String accountKey;
	private String bingUrl = "https://api.datamarket.azure.com/Bing/Search/Web?";
	private String query;
	private int precision = 10;
	private String format = "JSON";
	private int round = 0;
	private PriorityQueue<QueryItem> items;
	
	public BingSearch(String accountKey) {
		this.accountKey = accountKey;
		items = new PriorityQueue<QueryItem>();
	}
	
	public String search() throws IOException {
		round++;
		byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
		String accountKeyEnc = new String(accountKeyBytes);

		bingUrl += "Query=%27" + query + "%27&$top=" 
				+ precision + "&$format=" + format;
		URL url = new URL(bingUrl);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);

//		InputStream inputStream = (InputStream) urlConnection.getContent();
//		byte[] contentRaw = new byte[urlConnection.getContentLength()];
//		inputStream.read(contentRaw);
//		String content = new String(contentRaw);
//		
//		return content;
		
		InputStream is = urlConnection.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] contentRaw = new byte[urlConnection.getContentLength()];
		bis.read(contentRaw);
		String content = new String(contentRaw);
		
		return content;
	}
	
	public void record(String content) throws IOException {
		File outfile = new File("response.txt");
		if (!outfile.exists()) {
			outfile.createNewFile();
		}
		
		FileWriter fw = new FileWriter(outfile.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("Round " + round + "\n");
		bw.write(content+"\n");
		
		bw.close();		
		System.out.println("wrote the response into file");
	}
	
	public void parseJSON(String content) throws JSONException, IOException {
		JSONTokener jtokener = new JSONTokener(content);
		JSONObject jobject = new JSONObject(jtokener);
		JSONArray jarray = jobject.getJSONObject("d").getJSONArray("results");
		
		for (int i = 0; i < jarray.length(); i++) {
			String title = jarray.getJSONObject(i).getString("Title");
			String dep = jarray.getJSONObject(i).getString("Description");
			String link = jarray.getJSONObject(i).getString("Url");
			
			QueryItem qitem = new QueryItem();
			qitem.setTitle(title);
			qitem.setDescription(dep);
			qitem.setLind(link);
			
			items.add(qitem);
			
		}
		
		record(content);
		
	}
	
	public String getBingUrl() {
		return bingUrl;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public static void main(String[] args) throws IOException, JSONException {
		final String TEST_ACCOUNT_KEY = "5I28jnKDGBOqmvObm7cADuXDIrb/815SvhxsqChs8MI";
		
//		BingSearch bs;
		
//		if (args.length == 2) {
//			// convenient for testing
//			bs = new BingSearch(TEST_ACCOUNT_KEY);
//			bs.setPrecision(Integer.valueOf(args[0]));
//			bs.setQuery(args[1]);
//		} else if (args.length == 3) {
//			bs = new BingSearch(args[0]);
//			bs.setPrecision(Integer.valueOf(args[1]));
//			bs.setQuery(args[2]);
//		} else {
//			System.out.println("Please input three arguments");
//			System.exit(1);
//			return;
//		}
	
		BingSearch bs = new BingSearch(TEST_ACCOUNT_KEY);
		bs.setPrecision(10);
		bs.setQuery("gates");
		
		String content = bs.search();
		bs.parseJSON(content);
		
		System.out.println("Done");
		
	}

}
