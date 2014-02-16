package adbone;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.BufferedInputStream;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class BingSearch {
	private String accountKey;
	private String bingUrl;
	private String query;
	private int precision = 10;
	private String format = "JSON";
	private int round = 0;
	private PriorityQueue<QueryItem> items;
	private Map<String, Integer> dictionary;
	private Scanner scanIn;
	
	public BingSearch(String accountKey) throws IOException {
		this.accountKey = accountKey;
		items = new PriorityQueue<QueryItem>(precision, 
				new Comparator<QueryItem>() {
			public int compare(QueryItem qitem1, QueryItem qitem2) {
				if (qitem1.getRank() < qitem2.getRank()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		dictionary = new HashMap<String, Integer>();
		scanIn = new Scanner(System.in);
	}
	
	public String search() throws IOException {
		round++;
		byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
		String accountKeyEnc = new String(accountKeyBytes);

		bingUrl = "https://api.datamarket.azure.com/Bing/Search/Web?" + 
				"Query=%27" + query + "%27&$top=" 
				+ precision + "&$format=" + format;
		URL url = new URL(bingUrl);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
		
		InputStream is = urlConnection.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] contentRaw = new byte[urlConnection.getContentLength()];
		bis.read(contentRaw);
		String content = new String(contentRaw);
		
		System.out.format("Round %d\n", round);
		
		return content;
	}
	
	public double parseJSON(String content) throws JSONException, IOException {
		JSONTokener jtokener = new JSONTokener(content);
		JSONObject jobject = new JSONObject(jtokener);
		JSONArray jarray = jobject.getJSONObject("d").getJSONArray("results");
		
		int yes = 0;
		for (int i = 0; i < jarray.length(); i++) {
			String title = jarray.getJSONObject(i).getString("Title");
			String dep = jarray.getJSONObject(i).getString("Description");
			String link = jarray.getJSONObject(i).getString("Url");
			String type = jarray.getJSONObject(i).getJSONObject("__metadata").getString("type");
			
			// ignore non-html result
			if (!type.equals("WebResult")) {
				continue;
			}
			
			System.out.format("\nTitle: %s\n", title);
			System.out.format("Link: %s\n", link);
			System.out.format("Summary: %s\n\n", dep);
			System.out.format("Is this relevant? (Y/N)\t");
			
			String yesno = scanIn.nextLine();
			yesno = yesno.toLowerCase();
			while (!yesno.equals("y") && !yesno.equals("n") && !yesno.equals("")) {
				System.out.format("Please input (Y/N)\t");
				yesno = scanIn.nextLine();
				yesno = yesno.toLowerCase();
			}
			
			QueryItem qitem = new QueryItem();
			String summary = title + " " + dep;
			qitem.setSummary(summary.toLowerCase());
			if (yesno.equals("y") || yesno.equals("")) {
				qitem.setRelevant(true);
				yes++;
			}
			
			items.add(qitem);
		}	

		double relevance = (double)yes/jarray.length();
		
		return relevance;
	}
	
	public void run() throws JSONException, IOException {
		double relevance;
		do {
			String content = search();
			relevance = parseJSON(content);
			
			System.out.format("\nRelevance: %s\n\n", relevance);
			
			calculate();
			
			
			// update query
			
		} while (relevance < 0.9 && relevance > 0.01);
	}
	
	private void calculate() {
		for (QueryItem iter: items) {
			
		}
	}
	
	public void close() {
		scanIn.close();
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
		
		bs.run();
		
		System.out.println("Done");
		
	}

}
