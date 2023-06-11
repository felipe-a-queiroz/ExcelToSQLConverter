package com.example;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class ExcelToSqlConverter {
    public static void main(String[] args) {
        String currentDirectory = new File(".").getAbsolutePath();
        System.out.println("Diretório atual: " + currentDirectory);

        Properties properties = loadConfigProperties("config.properties");

        if (properties == null) {
            System.out.println("Não foi possível carregar o arquivo de configuração.");
            return;
        }

        String inputDirectory = properties.getProperty("inputDirectory");
        String outputDirectory = properties.getProperty("outputDirectory");

        File folder = new File(inputDirectory);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xlsx"));

        for (File file : files) {
            String fileName = FilenameUtils.removeExtension(file.getName());
            System.out.println("Arquivo encontrado: " + fileName);
            String outputFilePath = outputDirectory + File.separator + fileName + ".sql";

            try (Workbook workbook = new XSSFWorkbook(file);
                 FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                int sheetCount = workbook.getNumberOfSheets();

                for (int i = 0; i < sheetCount; i++) {
                    Sheet sheet = workbook.getSheetAt(i);
                    Iterator<Row> rowIterator = sheet.iterator();

                    if (rowIterator.hasNext()) {
                        Row tableNameRow = rowIterator.next();
                        Row columnNamesRow = rowIterator.next();

                        String tableName = getCellValue(tableNameRow.getCell(0));
                        System.out.println("Tabela encontrada: " + tableName);
                        StringBuilder sqlBuilder = new StringBuilder();

                        while (rowIterator.hasNext()) {
                            Row dataRow = rowIterator.next();
                            CellStyle cellStyle = dataRow.getCell(0).getCellStyle();

                            Cell cell = dataRow.getCell(0);
                            String cellValue = getCellValue(cell);
                            System.out.println("Linha encontrada: " + cellValue);
                            if (isGreen(cellStyle)) {
                                System.out.println("É verde");
                                String deleteSql = generateDeleteStatement(tableName, columnNamesRow, dataRow);
                                String insertSql = generateInsertStatement(tableName, columnNamesRow, dataRow);
                                sqlBuilder.append(deleteSql).append("\n");
                                sqlBuilder.append("/").append("\n");
                                sqlBuilder.append(insertSql).append("\n");
                                sqlBuilder.append("/").append("\n");
                            }
                        }

                        fos.write(sqlBuilder.toString().getBytes());
                    }
                }
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private static Properties loadConfigProperties(String configFilePath) {
        Properties properties = new Properties();
        File configFile = new File(configFilePath);

        try (FileInputStream input = new FileInputStream(configFile)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            properties = null;
        }

        return properties;
    }

    private static String getCellValue(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }

    private static boolean isGreen(CellStyle cellStyle) {
        Color backgroundColor = cellStyle.getFillForegroundColorColor();
        if (backgroundColor instanceof XSSFColor) {
            byte[] rgb = ((XSSFColor) backgroundColor).getRGB();
            return rgb[0] == 0 && rgb[1] == (byte) 255 && rgb[2] == 0;
        }
        return false;
    }

    private static String generateDeleteStatement(String tableName, Row columnNamesRow, Row dataRow) {
        StringBuilder deleteSql = new StringBuilder("DELETE FROM " + tableName + " WHERE ");

        int columnCount = columnNamesRow.getLastCellNum();

        for (int i = 0; i < columnCount; i++) {
            String columnName = getCellValue(columnNamesRow.getCell(i));
            String columnValue = getCellValue(dataRow.getCell(i));

            deleteSql.append(columnName).append(" = '").append(columnValue).append("'");

            if (i < columnCount - 1) {
                deleteSql.append(" AND ");
            }
        }
        deleteSql.append(";");
        return deleteSql.toString();
    }

    private static String generateInsertStatement(String tableName, Row columnNamesRow, Row dataRow) {
        StringBuilder insertBuilder = new StringBuilder();
        insertBuilder.append("INSERT INTO ").append(tableName).append(" (");

        int columnCount = columnNamesRow.getLastCellNum();
        for (int i = 0; i < columnCount; i++) {
            String columnName = getCellValue(columnNamesRow.getCell(i));
            insertBuilder.append(columnName);

            if (i < columnCount - 1) {
                insertBuilder.append(", ");
            }
        }

        insertBuilder.append(") VALUES (");

        for (int i = 0; i < columnCount; i++) {
            Cell cell = dataRow.getCell(i);
            String cellValue = getCellValue(cell);
            insertBuilder.append("'").append(cellValue).append("'");

            if (i < columnCount - 1) {
                insertBuilder.append(", ");
            }
        }

        insertBuilder.append(");");
        return insertBuilder.toString();
    }
}
