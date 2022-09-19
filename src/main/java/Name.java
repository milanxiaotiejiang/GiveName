import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: milan
 * Time: 2022/9/19 10:17
 * Des:
 */
public class Name {
    public static final String REQUEST_URL = "https://www.name321.com/xmdf/";

    public static final OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        String[] arrays = {"禾", "婉", "芸", "舒", "思", "悦", "恬", "栖", "景", "航", "瑞", "锦", "凯",
                "珺", "瑜", "松", "衿", "朗", "玮", "岚", "栩", "屹", "霖", "璟", "彦", "翎", "屿"};

        List<String> list = transformation(arrays);
        for (String s : list) {
            System.out.println(s);
        }

        System.out.println("----------------------------------");

        List<Honor> honors = new ArrayList<>();
        for (String name : list) {
            Honor honor = new Honor();
            honor.name = name;
            honor.mark = requestUrl(name);
            honors.add(honor);
        }

        Collections.sort(honors, new Comparator<Honor>() {
            @Override
            public int compare(Honor o1, Honor o2) {
                return o1.mark - o2.mark;
            }
        });

        List<String> name100 = new ArrayList<>();
        for (Honor honor : honors) {
            System.out.println(honor);
            if (honor.mark == 100) {
                name100.add(honor.name);
            }
        }

        System.out.println(arrays.length);
        for (String s : name100) {
            System.out.print(s + " ");
        }

        System.out.println();
        System.out.println((System.currentTimeMillis() - startTime) + "ms");
    }

    public static int requestUrl(String name) {
        try {
            String result = post(REQUEST_URL, name);

            Document doc = Jsoup.parse(result);
            Elements content = doc.getElementsByClass("read-content");
            Element element = content.get(0);
            Elements childrens = element.children();
            Element last = childrens.last();
            Elements childrenMark = last.children();
            Element elementMark = childrenMark.get(0);
            String data = elementMark.text();
            return Integer.valueOf(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<String> transformation(String[] arrays) {
        List<String> results = new ArrayList<>();
        for (String a : arrays) {
            for (String b : arrays) {
                if (!a.equals(b)) {
                    results.add(a + b);
                }
            }
        }
        return results;
    }

    public static String post(String url, String name) throws IOException {
        RequestBody body = new FormBody.Builder().add("xm", "张" + name).add("dxfx", "1").build();
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    static class Honor {
        public String name;
        public int mark;

        @Override
        public String toString() {
            return "Honor{" +
                    "name='" + name + '\'' +
                    ", mark=" + mark +
                    '}';
        }
    }
}
