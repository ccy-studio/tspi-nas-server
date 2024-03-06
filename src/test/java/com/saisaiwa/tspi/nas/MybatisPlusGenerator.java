package com.saisaiwa.tspi.nas;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;

import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/1/31 14:40
 * @Version：1.0
 */
public class MybatisPlusGenerator {

    private static final List<String> GENERATOR_TABLES = List.of(
            "t_buckets"
    );

    public static void main(String[] args) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder("jdbc:mysql://43.142.172.195:3301/tspi_nas?allowMultiQueries=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true&useAffectedRows=true", "root", "saisaiwa.com").build();
        AutoGenerator generator = new AutoGenerator(dataSourceConfig);
        generator.strategy(new StrategyConfig.Builder()
                .addTablePrefix("t_")
                .addInclude("^t_.*")
                .entityBuilder()
                .enableFileOverride()
                .disableSerialVersionUID()
                .enableTableFieldAnnotation()
                .idType(IdType.AUTO)
                .enableLombok()
                .build());
        generator.template(new TemplateConfig.Builder()
                .disable(TemplateType.CONTROLLER, TemplateType.SERVICE, TemplateType.SERVICE_IMPL)
                .build());
        generator.global(new GlobalConfig.Builder()
                .outputDir("D:\\MyTempProjects\\tspi-nas-server\\src\\test\\java\\com\\saisaiwa\\tspi\\nas")
                .author("Saisaiwa")
                .disableOpenDir()
                .build());
        generator.packageInfo(new PackageConfig.Builder()
                .parent("com.saisaiwa.tspi.nas")
                .entity("domain.entity")
                .mapper("mapper")
                .build());
        generator.execute();
    }

}
