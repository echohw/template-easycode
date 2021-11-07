package com.example.ectool.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.ectool.EcToolApp;
import com.example.ectool.beans.SubItemConfig;
import com.jayway.jsonpath.JsonPath;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jooq.lambda.Unchecked;
import org.yaml.snakeyaml.Yaml;

/**
 * Created by AMe on 2021-10-29 10:46.
 */
public class BaseConfig {
    
    public static final String CONFIG_FILE = "EasyCodeConfig.json";
    
    public static final String USER_DIR = System.getProperty("user.dir");
    
    public static final String DIR_ASS = "assdisdir"; // assdir
    public static final String DIR_ASS_ABS = USER_DIR + "/../" + DIR_ASS;
    public static final String DIR_DIS = "assdisdir"; // disdir
    public static final String DIR_DIS_ABS = USER_DIR + "/../" + DIR_DIS;
    
    public static final String CONFIG_ITEM_COLUMN_CONFIG = "columnConfig";
    public static final String CONFIG_ITEM_GLOBAL_CONFIG = "globalConfig";
    public static final String CONFIG_ITEM_TEMPLATE = "template";
    public static final String CONFIG_ITEM_TYPE_MAPPER = "typeMapper";
    public static final List<String> ASS_SCAN_DIRS = Arrays.asList(CONFIG_ITEM_COLUMN_CONFIG, CONFIG_ITEM_GLOBAL_CONFIG, CONFIG_ITEM_TEMPLATE, CONFIG_ITEM_TYPE_MAPPER);
    public static final List<String> DIS_RESO_DIRS = Arrays.asList(CONFIG_ITEM_COLUMN_CONFIG, CONFIG_ITEM_GLOBAL_CONFIG, CONFIG_ITEM_TEMPLATE, CONFIG_ITEM_TYPE_MAPPER);
    
    public static final Map<String, Map<String, List<String>>> FILE_SORT = initFileSortProps();
    
    public static final Map<String, Function<Path, Map<String, SubItemConfig>>> ASS_SCAN_DIR_FUNC_MAP = new HashMap<String, Function<Path, Map<String, SubItemConfig>>>() {{
        put(CONFIG_ITEM_COLUMN_CONFIG, Unchecked.function(path -> { // configItemPath
            Function<String, String> extractGroup = fileName -> fileName.replaceFirst("\\.json$", "");
            return Files.list(path).filter(Files::isRegularFile).collect(Collectors.toMap(filePath -> extractGroup.apply(filePath.getFileName().toString()), Unchecked.function(filePath -> {
                String fileContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
                List<Map<String, Object>> elementList = JSON.parseObject(fileContent, new TypeReference<List<Map<String, Object>>>() {});
                return new SubItemConfig(extractGroup.apply(filePath.getFileName().toString()), elementList);
            })));
        }));
        put(CONFIG_ITEM_GLOBAL_CONFIG, Unchecked.function(path -> {
            return Files.list(path).filter(Files::isDirectory).collect(Collectors.toMap(groupPath -> groupPath.getFileName().toString(), Unchecked.function(groupPath -> {
                List<String> sortReferFiles = Optional.ofNullable(FILE_SORT.get(path.getFileName().toString()))
                    .map(groupFilesMap -> groupFilesMap.getOrDefault(groupPath.getFileName().toString(), groupFilesMap.get("Common")))
                    .orElse(Collections.emptyList());
                List<Map<String, Object>> elementList = Files.list(groupPath).filter(Files::isRegularFile).sorted((o1, o2) -> {
                    int o1Idx = sortReferFiles.indexOf(o1.getFileName().toString());
                    int o2Idx = sortReferFiles.indexOf(o2.getFileName().toString());
                    return (o1Idx != -1 ? o1Idx : sortReferFiles.size()) - (o2Idx != -1 ? o2Idx : sortReferFiles.size());
                }).map(Unchecked.function(filePath -> {
                    return (Map<String, Object>) new HashMap<String, Object>() {{
                        put("name", filePath.getFileName().toString());
                        put("value", new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8));
                    }};
                })).collect(Collectors.toList());
                return new SubItemConfig(groupPath.getFileName().toString(), elementList);
            })));
        }));
        put(CONFIG_ITEM_TEMPLATE, Unchecked.function(path -> {
            return Files.list(path).filter(Files::isDirectory).collect(Collectors.toMap(groupPath -> groupPath.getFileName().toString(), Unchecked.function(groupPath -> {
                List<String> sortReferFiles = Optional.ofNullable(FILE_SORT.get(path.getFileName().toString()))
                    .map(groupFilesMap -> groupFilesMap.getOrDefault(groupPath.getFileName().toString(), groupFilesMap.get("Common")))
                    .orElse(Collections.emptyList());
                List<Map<String, Object>> elementList = Files.list(groupPath).filter(Files::isRegularFile).sorted((o1, o2) -> {
                        int o1Idx = sortReferFiles.indexOf(o1.getFileName().toString());
                        int o2Idx = sortReferFiles.indexOf(o2.getFileName().toString());
                        return (o1Idx != -1 ? o1Idx : sortReferFiles.size()) - (o2Idx != -1 ? o2Idx : sortReferFiles.size());
                }).map(Unchecked.function(filePath -> {
                    return (Map<String, Object>) new HashMap<String, Object>() {{
                        put("name", filePath.getFileName().toString());
                        put("code", new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8));
                    }};
                })).collect(Collectors.toList());
                return new SubItemConfig(groupPath.getFileName().toString(), elementList);
            })));
        }));
        put(CONFIG_ITEM_TYPE_MAPPER, Unchecked.function(path -> {
            Function<String, String> extractGroup = fileName -> fileName.replaceFirst("\\.json$", "");
            return Files.list(path).filter(Files::isRegularFile).collect(Collectors.toMap(filePath -> extractGroup.apply(filePath.getFileName().toString()), Unchecked.function(filePath -> {
                String fileContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
                List<Map<String, Object>> elementList = JSON.parseObject(fileContent, new TypeReference<List<Map<String, Object>>>() {});
                return new SubItemConfig(extractGroup.apply(filePath.getFileName().toString()), elementList);
            })));
        }));
    }};
    
    public static final Map<String, BiConsumer<Path, Map<String, SubItemConfig>>> DIS_RESO_DIR_FUNC_MAP = new HashMap<String, BiConsumer<Path, Map<String, SubItemConfig>>>() {{
        put(CONFIG_ITEM_COLUMN_CONFIG, Unchecked.biConsumer((path, groupConfigMap) -> { // configItemPath
            Files.createDirectories(path);
            for (Entry<String, SubItemConfig> entry : groupConfigMap.entrySet()) {
                String fileName = entry.getKey() + ".json";
                Files.write(path.resolve(fileName), JSON.toJSONString(entry.getValue().getElementList(), true).getBytes(StandardCharsets.UTF_8));
            }
        }));
        put(CONFIG_ITEM_GLOBAL_CONFIG, Unchecked.biConsumer((path, groupConfigMap) -> {
            for (Entry<String, SubItemConfig> entry : groupConfigMap.entrySet()) {
                Files.createDirectories(path.resolve(entry.getKey()));
                for (Map<String, Object> element : entry.getValue().getElementList()) {
                    String fileName = (String) element.get("name");
                    String fileContent = (String) element.get("value");
                    Files.write(path.resolve(Paths.get(entry.getKey(), fileName)), fileContent.getBytes(StandardCharsets.UTF_8));
                }
            }
        }));
        put(CONFIG_ITEM_TEMPLATE, Unchecked.biConsumer((path, groupConfigMap) -> {
            for (Entry<String, SubItemConfig> entry : groupConfigMap.entrySet()) {
                Files.createDirectories(path.resolve(entry.getKey()));
                for (Map<String, Object> element : entry.getValue().getElementList()) {
                    String fileName = (String) element.get("name");
                    String fileContent = (String) element.get("code");
                    Files.write(path.resolve(Paths.get(entry.getKey(), fileName)), fileContent.getBytes(StandardCharsets.UTF_8));
                }
            }
        }));
        put(CONFIG_ITEM_TYPE_MAPPER, Unchecked.biConsumer((path, groupConfigMap) -> {
            Files.createDirectories(path);
            for (Entry<String, SubItemConfig> entry : groupConfigMap.entrySet()) {
                String fileName = entry.getKey() + ".json";
                Files.write(path.resolve(fileName), JSON.toJSONString(entry.getValue().getElementList(), true).getBytes(StandardCharsets.UTF_8));
            }
        }));
    }};
    
    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, List<String>>> initFileSortProps() {
        try (
            InputStream inputStream = EcToolApp.class.getClassLoader().getResourceAsStream("application.yml");
        ) {
            return (Map<String, Map<String, List<String>>>) Optional.ofNullable(inputStream)
                .map(is -> new Yaml().load(is))
                .map(obj -> JsonPath.read(obj, "$.ectool.ass.filesort"))
                .orElse(Collections.emptyMap());
        } catch (Exception ex) {
            System.out.println("文件排序配置读取失败，" + ex.getMessage());
            return Collections.emptyMap();
        }
    }
}
