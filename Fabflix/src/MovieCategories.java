import java.util.Map;
import java.util.HashMap;

public class MovieCategories {

    private final Map<String, String> categoryMap;

    public MovieCategories() {
        categoryMap = new HashMap<>();
        initializeCategories();
    }

    private void initializeCategories() {
        categoryMap.put("Susp", "Thriller");//把xml中的Susp这个电影类别存为Thriller
        categoryMap.put("CnR", "Cops and Robbers");
        categoryMap.put("CnRb", "Cops and Robbers");
        categoryMap.put("Dram", "Drama");
        categoryMap.put("West", "Western");
        categoryMap.put("Myst", "Mystery");
        categoryMap.put("S.F.", "Sci-Fi");
        categoryMap.put("ScFi", "Sci-Fi");
        categoryMap.put("SciF", "Sci-Fi");
        categoryMap.put("Advt", "Adventure");
        categoryMap.put("Horr", "Horror");
        categoryMap.put("Romt", "Romantic");
        categoryMap.put("Comd", "Comedy");
        categoryMap.put("Musc", "Musical");
        categoryMap.put("Docu", "Documentary");
        categoryMap.put("Porn", "Pornography");
        categoryMap.put("Noir", "Black");
        categoryMap.put("BioP", "Biographical Picture");
        categoryMap.put("TV", "TV show");
        categoryMap.put("TVs", "TV series");
        categoryMap.put("TVm", "TV miniseries");
        categoryMap.put("Actn", "Action");
    }

    public String findCategory(String code) {
        return categoryMap.getOrDefault(code, "Unknown");
    }
}
