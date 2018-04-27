package generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Generator {

	public static void main(String[] args) {

		String url = "http://www.samfunnskunnskap.no/?page_id=181&lang=no";
		List<String> urls = new ArrayList<String>();
		Document dc = getDocument(url);
		Elements hrefs = dc.select(".content-holder a[href]");
		hrefs.forEach(element -> {
			String cur = element.attr("href");
			if (!cur.equals(url))
				urls.add(cur);
		});
		urls.forEach(a -> System.out.println(a));

		Element content = update(dc);

		for (String iter : urls) {
			Document doc = getDocument(iter);
			if (null != doc) {
				Elements child = update(doc).select(">*");
				content.append(child.html());
			}
		}
		rewrite(dc);
		return;
	}

	private static void rewrite(Document doc) {
		FileWriter writer = null;
		try {
			writer = new FileWriter("result-no.html");
			writer.write(doc.outerHtml());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private static Element update(Document doc) {
		Elements elements = doc.select("#header, #footer, .panel, .bredcrumbs-holder, #sidebar");
		elements.remove();
		Elements results = doc.select("div[dir]");
		results.removeClass("content-frame");
		results.select("#content").removeAttr("id");
		doc.select("*[src^='http://www.youtube.com/']").remove();
		elements = doc.select("#aside");
		elements.removeAttr("id");
		elements.select(".post").remove();
		return results.get(0);
	}

	private static Document getDocument(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

}
