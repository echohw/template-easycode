package com.example.ectool.handler;

import static com.example.ectool.config.BaseConfig.ASS_SCAN_DIRS;
import static com.example.ectool.config.BaseConfig.ASS_SCAN_DIR_FUNC_MAP;
import static com.example.ectool.config.BaseConfig.CONFIG_FILE;
import static com.example.ectool.config.BaseConfig.CONFIG_ITEM_COLUMN_CONFIG;
import static com.example.ectool.config.BaseConfig.CONFIG_ITEM_GLOBAL_CONFIG;
import static com.example.ectool.config.BaseConfig.CONFIG_ITEM_TEMPLATE;
import static com.example.ectool.config.BaseConfig.CONFIG_ITEM_TYPE_MAPPER;
import static com.example.ectool.config.BaseConfig.DIR_ASS;
import static com.example.ectool.config.BaseConfig.DIR_ASS_ABS;

import com.alibaba.fastjson.JSON;
import com.example.ectool.beans.EasyCodeConfig;
import com.example.ectool.beans.SubItemConfig;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 扫描路径下的文件,并装配为EasyCodeConfig.json
 * Created by AMe on 2021-09-22 20:53.
 */
public class AssembleHandler implements Handler {
    
    @Override
    public void handle() throws Exception {
        if (Files.notExists(Paths.get(DIR_ASS_ABS)) || !Files.isDirectory(Paths.get(DIR_ASS_ABS))) {
            throw new RuntimeException(DIR_ASS + "目录不存在");
        }
        Path configFilePath = Paths.get(DIR_ASS_ABS, CONFIG_FILE);
        EasyCodeConfig oriEasyCodeConfig = null;
        if (Files.exists(configFilePath) && Files.isRegularFile(configFilePath)) {
            String configJsonStr = new String(Files.readAllBytes(configFilePath), StandardCharsets.UTF_8);
            oriEasyCodeConfig = JSON.parseObject(configJsonStr, EasyCodeConfig.class);
        }
        EasyCodeConfig newEasyCodeConfig = scanAssDirToBuildConfig();
        if (oriEasyCodeConfig != null) {
            newEasyCodeConfig.setAuthor(oriEasyCodeConfig.getAuthor());
            newEasyCodeConfig.setVersion(oriEasyCodeConfig.getVersion());
            newEasyCodeConfig.setUserSecure(oriEasyCodeConfig.getUserSecure());
            newEasyCodeConfig.setCurrTypeMapperGroupName(oriEasyCodeConfig.getCurrTypeMapperGroupName());
            newEasyCodeConfig.setCurrTemplateGroupName(oriEasyCodeConfig.getCurrTemplateGroupName());
            newEasyCodeConfig.setCurrColumnConfigGroupName(oriEasyCodeConfig.getCurrColumnConfigGroupName());
            newEasyCodeConfig.setCurrGlobalConfigGroupName(oriEasyCodeConfig.getCurrGlobalConfigGroupName());
        }
        Files.write(configFilePath, JSON.toJSONString(newEasyCodeConfig).replaceAll("\\\\r\\\\n", "\\\\n").getBytes(StandardCharsets.UTF_8));
    }
    
    private EasyCodeConfig scanAssDirToBuildConfig() {
        Map<String, Map<String, SubItemConfig>> scanDirGroupConfigMap = new LinkedHashMap<>();
        for (String scanDir : ASS_SCAN_DIRS) {
            Path scanPath = Paths.get(DIR_ASS_ABS, scanDir);
            Function<Path, Map<String, SubItemConfig>> assFunc = ASS_SCAN_DIR_FUNC_MAP.get(scanDir);
            if (Files.isDirectory(scanPath) && assFunc != null) {
                Map<String, SubItemConfig> groupConfigMap = assFunc.apply(scanPath);
                scanDirGroupConfigMap.put(scanDir, groupConfigMap);
            }
        }
        EasyCodeConfig easyCodeConfig = new EasyCodeConfig();
        easyCodeConfig.setColumnConfig(scanDirGroupConfigMap.get(CONFIG_ITEM_COLUMN_CONFIG));
        easyCodeConfig.setGlobalConfig(scanDirGroupConfigMap.get(CONFIG_ITEM_GLOBAL_CONFIG));
        easyCodeConfig.setTemplate(scanDirGroupConfigMap.get(CONFIG_ITEM_TEMPLATE));
        easyCodeConfig.setTypeMapper(scanDirGroupConfigMap.get(CONFIG_ITEM_TYPE_MAPPER));
        return easyCodeConfig;
    }
}
