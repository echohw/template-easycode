package com.example.ectool.beans;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;

/**
 * Created by AMe on 2021-09-22 20:54.
 */
@Data
public class EasyCodeConfig implements Serializable {
    
    private String author;
    private String version;
    private String userSecure;
    private String currTypeMapperGroupName;
    private String currTemplateGroupName;
    private String currColumnConfigGroupName;
    private String currGlobalConfigGroupName;
    private Map<String, SubItemConfig> typeMapper;
    private Map<String, SubItemConfig> template;
    private Map<String, SubItemConfig> columnConfig;
    private Map<String, SubItemConfig> globalConfig;
}
