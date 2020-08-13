package com.mybatisv2.utils;

import com.mybatisv2.framework.sqlnode.*;
import com.mybatisv2.framework.sqlsource.DynamicSqlSource;
import com.mybatisv2.framework.sqlsource.RawSqlSource;
import com.mybatisv2.framework.sqlsource.SqlSource;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class LoadXmlUtils {

    //读取xml中mapper信息
    public static List<String> loadMapperXml(String location) throws IOException, DocumentException {
        try (InputStream inputStream = getResourceAsStream(location)) {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(inputStream);
            Element rootElement = document.getRootElement();
            List<Element> mapperList = rootElement.element("mappers").elements("mapper");
            return mapperList.stream().map(element -> element.attributeValue("resource")).collect(Collectors.toList());
        }
    }

    public static InputStream getResourceAsStream(String location) {
        return LoadXmlUtils.class.getClassLoader().getResourceAsStream(location);
    }

    //读取xml中数据配置信息
    public static Properties loadDataSourceXML(String location) throws IOException, DocumentException {
        // 获取全局配置文件对应的流对象
        try (InputStream inputStream = getResourceAsStream(location)) {
            // 获取Document对象
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(inputStream);
            Element rootElement = document.getRootElement();
            Element environments = rootElement.element("environments");
            String attribute = environments.attributeValue("default");
            List<Element> elements = environments.elements("environment");
            if (null == elements || elements.isEmpty()) {
                throw new RuntimeException("无法找到environment节点,无法获取到数据配置信息");
            }
            Optional<Element> optional = elements.stream().filter(element -> null == attribute || attribute.equals(element.attributeValue("id"))).findFirst();
            Element dataSource = (optional.isPresent() ? optional.get() : elements.get(0)).element("dataSource");
            switch (dataSource.attributeValue("type")) {
                case "DBCP":
                default:
                    List<Element> sourceElement = dataSource.elements("property");
                    Properties properties = new Properties();
                    for (Element source : sourceElement) {
                        properties.put(source.attributeValue("name"), source.attributeValue("value"));
                    }
                    return properties;
            }
        }
    }

    public static SqlSource createSqlSource(Element selectElement) {
        SqlNode mixedSqlNode = parseDynamicTags(selectElement);
        if (mixedSqlNode instanceof StaticTextSqlNode) {
            return new RawSqlSource(mixedSqlNode);
        } else {
            return new DynamicSqlSource(mixedSqlNode);
        }
    }

    private static SqlNode parseDynamicTags(Element selectElement) {
        List<SqlNode> sqlNodes = new ArrayList<>();
        int nodeCount = selectElement.nodeCount();
        for (int i = 0; i < nodeCount; i++) {
            Node node = selectElement.node(i);
            if (node instanceof Text) {
                String text = node.getText();
                if (text == null || "".equals(text.trim())) {
                    continue;
                }
                TextSqlNode textSqlNode = new TextSqlNode(text.trim());
                if (textSqlNode.isDynamic()) {
                    return textSqlNode;
                } else {
                    return new StaticTextSqlNode(text.trim());
                }
            } else if (node instanceof Element) {
                Element element = (Element) node;
                String name = element.getName();
                if ("if".equals(name)) {
                    String test = element.attributeValue("test");
                    //递归去解析子元素
                    SqlNode sqlNode = parseDynamicTags(element);
                    IfSqlNode ifSqlNode = new IfSqlNode(test, sqlNode);
                    sqlNodes.add(ifSqlNode);
                } else {
                    // TODO
                }
            } else {
                //TODO
            }
        }
        return new MixedSqlNode(sqlNodes);
    }
}
