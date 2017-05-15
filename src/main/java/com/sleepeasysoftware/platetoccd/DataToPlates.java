package com.sleepeasysoftware.platetoccd;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.sleepeasysoftware.platetoccd.model.Plate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Daniel Kaplan on behalf of Sleep Easy Software.
 */
@Component
public class DataToPlates {
    private static final Logger log = LoggerFactory.getLogger(DataToPlates.class);

    private static final int PLATE_NAME_INDEX = 0;
    private static final int INPUT_ROWS_PER_PLATE = 18;

    public List<Plate> execute(List<List<Optional<String>>> inputData, List<String> ignoredColumns) {
        List<Plate> plates = new ArrayList<>();

        for (int inputRowIndex = 1; inputRowIndex < inputData.size(); inputRowIndex++) {
            List<Optional<String>> previousRow = inputData.get(inputRowIndex - 1);
            List<Optional<String>> currentRow = inputData.get(inputRowIndex);

            if (currentRowContainsPlateName(previousRow)) {
                List<String> columnNames = getColumnNames(currentRow);

                List<List<Optional<String>>> inputDataOfOnePlate = inputData.subList(inputRowIndex + 1, inputRowIndex + INPUT_ROWS_PER_PLATE - 1);

                Table<String, String, Optional<String>> plateData = HashBasedTable.create();
                String plateName = currentRow.get(0).orElse("(Plate Name Missing)");
                for (int rowIndex = 0; rowIndex < inputDataOfOnePlate.size(); rowIndex++) {
                    List<Optional<String>> inputPlateRow = inputDataOfOnePlate.get(rowIndex);

                    String plateRow = Character.toString(alphabeticRollover(rowIndex));
                    for (int columnIndex = 1; columnIndex < 25; columnIndex++) {
                        String plateColumn = columnNames.get(columnIndex - 1);
                        if (!ignoredColumns.contains(plateColumn)) {
                            try {
                                String formattedColumn = plateColumn;
                                if (plateColumn.length() == 1) {
                                    formattedColumn = "0" + plateColumn;
                                }

                                plateData.put(plateRow, formattedColumn, inputPlateRow.get(columnIndex));
                            } catch (IndexOutOfBoundsException e) {
                                log.error("Could not process plate named " + plateName);
                                throw e;
                            }

                        }

                    }
                }
                plates.add(new Plate(plateName, plateData));
            }
        }

        return plates;
    }

    private List<String> getColumnNames(List<Optional<String>> headerRow) {
        List<String> columnNames = Lists.newArrayList();
        for (int columnIndex = 1; columnIndex < 25; columnIndex++) {
            Optional<String> potentialColumnName = headerRow.get(columnIndex);
            if (potentialColumnName.isPresent()) {
                columnNames.add(potentialColumnName.get());
            } else {
                columnNames.add("COLUMN NAME NOT FOUND");
            }
        }
        return columnNames;
    }

    private boolean currentRowContainsPlateName(List<Optional<String>> previousRow) {
        return previousRow.get(0).isPresent() && "Measurement".equals(previousRow.get(PLATE_NAME_INDEX).get());
    }

    private char alphabeticRollover(int spreadsheetRow) {
        return (char) ((int) 'A' + spreadsheetRow);
    }
}
