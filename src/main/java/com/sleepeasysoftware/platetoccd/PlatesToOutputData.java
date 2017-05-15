package com.sleepeasysoftware.platetoccd;

import com.sleepeasysoftware.platetoccd.model.OutputDataRow;
import com.sleepeasysoftware.platetoccd.model.Plate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Daniel Kaplan on behalf of Sleep Easy Software.
 */
@Component
public class PlatesToOutputData {
    public List<OutputDataRow> execute(List<Plate> plates) {
        List<OutputDataRow> output = new ArrayList<>();
        int row = 1;
        for (Plate plate : plates) {
            for (String columnName : plate.getData().columnKeySet()) {
                for (String rowName : plate.getData().rowKeySet()) {
                    String plateName = plate.getName();
                    String well = rowName + columnName;
                    Optional<String> data = plate.getData().get(rowName, columnName);

                    output.add(new OutputDataRow(row, plateName, well, data));
                    row++;
                }
            }
        }

        return output;
    }
}
