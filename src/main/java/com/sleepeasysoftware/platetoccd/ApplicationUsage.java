package com.sleepeasysoftware.platetoccd;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import com.sleepeasysoftware.platetoccd.model.OutputDataRow;
import com.sleepeasysoftware.platetoccd.model.Plate;
import com.sleepeasysoftware.platetoccd.parser.ExcelParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Optional;

/**
 * Created by Daniel Kaplan on behalf of Sleep Easy Software.
 */
@Component
public class ApplicationUsage implements ApplicationRunner {


    static final String INCLUDE_ROW_COUNT = "--include-row-count";
    private final ExcelParser excelParser;
    private final DataToPlates dataToPlates;
    private final PlatesToOutputData platesToOutputData;

    @Autowired
    public ApplicationUsage(ExcelParser excelParser, DataToPlates dataToPlates, PlatesToOutputData platesToOutputData) {
        this.excelParser = excelParser;
        this.dataToPlates = dataToPlates;
        this.platesToOutputData = platesToOutputData;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> files = args.getNonOptionArgs();
        if (files.size() != 2) {
            throw new IllegalArgumentException("Incorrect usage:\n" +
                    "You need to pass in at least two arguments.  The first one is the\n" +
                    "path to the input file.  The second one is the path to the output file.  e.g.,\n" +
                    "java -jar 384w-plate-to-ccd.jar '/Users/pivotal/workspace/384w-plate-to-ccd/src/test/resources/happy_path_input.xlsx' '/Users/pivotal/workspace/384w-plate-to-ccd/src/test/resources/happy_path_output.xlsx'\n" +
                    "You can also pass in an optional argument named " + INCLUDE_ROW_COUNT + " that will place a \"ROW_COUNT\" column at the beginning.\n" +
                    "java -jar 384w-plate-to-ccd.jar " + INCLUDE_ROW_COUNT + " '/Users/pivotal/workspace/384w-plate-to-ccd/src/test/resources/happy_path_input.xlsx' '/Users/pivotal/workspace/384w-plate-to-ccd/src/test/resources/happy_path_output.xlsx'\n" +
                    "You can ignore columns by passing in --ignore-column arguments.  e.g.,\n" +
                    "java -jar --ignore-column=1 --ignore-column=2 --ignore-column=23 --ignore-column=24 384w-plate-to-ccd.jar '/Users/pivotal/workspace/384w-plate-to-ccd/src/test/resources/happy_path_input.xlsx' '/Users/pivotal/workspace/384w-plate-to-ccd/src/test/resources/happy_path_output.xlsx'"
            );
        }

        String inputPath = files.get(0);
        if (!new File(inputPath).exists()) {
            throw new IllegalArgumentException("Could not find the input file.  Looked for " + inputPath);
        }

        String outputPath = files.get(1);
        if (new File(outputPath).exists()) {
            throw new IllegalArgumentException("Output file already exists.  The output file must not already exist.  Found " + outputPath);
        }

        List<String> ignoredColumns = args.getOptionValues("ignore-column");
        if (ignoredColumns == null) {
            ignoredColumns = Lists.newArrayList();
        }

        List<List<Optional<String>>> inputData = excelParser.parseFirstSheet(inputPath);

        List<Plate> plates = dataToPlates.execute(inputData, ignoredColumns);

        List<OutputDataRow> outputData = platesToOutputData.execute(plates);

        boolean includeRowCount = args.containsOption(INCLUDE_ROW_COUNT.substring(2));

        try (CSVWriter writer = new CSVWriter(new FileWriter(outputPath))) {
            String[] header;
            if (includeRowCount) {
                header = new String[]{"Row Count", "Plate", "Well", "Data"};
            } else {
                header = new String[]{"Plate", "Well", "Data"};
            }
            writer.writeNext(header);

            for (int i = 0; i < outputData.size(); i++) {
                OutputDataRow outputRow = outputData.get(i);
                String[] rawRow;
                if (includeRowCount) {
                    rawRow = new String[]{(i + 1) + "", outputRow.getPlateName(), outputRow.getWell(), outputRow.getData().orElse("")};
                } else {
                    rawRow = new String[]{outputRow.getPlateName(), outputRow.getWell(), outputRow.getData().orElse("")};
                }
                writer.writeNext(rawRow);
            }
        }
    }
}
