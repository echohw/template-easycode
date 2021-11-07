package com.example.ectool.handler;

import static com.example.ectool.config.BaseConfig.CONFIG_FILE;
import static com.example.ectool.config.BaseConfig.CONFIG_ITEM_COLUMN_CONFIG;
import static com.example.ectool.config.BaseConfig.CONFIG_ITEM_GLOBAL_CONFIG;
import static com.example.ectool.config.BaseConfig.CONFIG_ITEM_TEMPLATE;
import static com.example.ectool.config.BaseConfig.CONFIG_ITEM_TYPE_MAPPER;
import static com.example.ectool.config.BaseConfig.DIR_DIS_ABS;
import static com.example.ectool.config.BaseConfig.DIS_RESO_DIRS;
import static com.example.ectool.config.BaseConfig.DIS_RESO_DIR_FUNC_MAP;

import com.alibaba.fastjson.JSON;
import com.example.ectool.beans.EasyCodeConfig;
import com.example.ectool.beans.SubItemConfig;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 解析EasyCodeConfig.json文件,并写入到路径下
 * Created by AMe on 2021-09-22 20:53.
 */
public class DisassembleHandler implements Handler {
    
    @Override
    public void handle() throws Exception {
        Path configFilePath = Paths.get(DIR_DIS_ABS, CONFIG_FILE);
        if (Files.notExists(configFilePath) || !Files.isRegularFile(configFilePath)) {
            throw new RuntimeException(CONFIG_FILE + "文件不存在");
        }
        String configJsonStr = new String(Files.readAllBytes(configFilePath), StandardCharsets.UTF_8);
        EasyCodeConfig easyCodeConfig = JSON.parseObject(configJsonStr, EasyCodeConfig.class);
        iterDisDirToResolveConfig(easyCodeConfig);
    }
    
    private void iterDisDirToResolveConfig(EasyCodeConfig easyCodeConfig) {
        Map<String, Map<String, SubItemConfig>> resoDirGroupConfigMap = new LinkedHashMap<>();
        resoDirGroupConfigMap.put(CONFIG_ITEM_COLUMN_CONFIG, easyCodeConfig.getColumnConfig());
        resoDirGroupConfigMap.put(CONFIG_ITEM_GLOBAL_CONFIG, easyCodeConfig.getGlobalConfig());
        resoDirGroupConfigMap.put(CONFIG_ITEM_TEMPLATE, easyCodeConfig.getTemplate());
        resoDirGroupConfigMap.put(CONFIG_ITEM_TYPE_MAPPER, easyCodeConfig.getTypeMapper());
        for (String resoDir : DIS_RESO_DIRS) {
            Map<String, SubItemConfig> groupConfigMap = resoDirGroupConfigMap.get(resoDir);
            BiConsumer<Path, Map<String, SubItemConfig>> disFunc = DIS_RESO_DIR_FUNC_MAP.get(resoDir);
            if (disFunc != null) {
                disFunc.accept(Paths.get(DIR_DIS_ABS, resoDir), groupConfigMap);
            }
        }
    }
}
