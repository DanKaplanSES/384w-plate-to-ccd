package com.sleepeasysoftware.platetoccd;

import com.sleepeasysoftware.platetoccd.parser.CsvParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.List;
import java.util.Optional;

import static com.sleepeasysoftware.platetoccd.ApplicationUsageTest.EXISTING_INPUT_FILE;
import static com.sleepeasysoftware.platetoccd.FileDelete.deleteAndFlushFs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by Daniel Kaplan on behalf of Sleep Easy Software.
 */
public class AcceptanceTest {

    private static final String OUTPUT_FILE = "src/test/resources/test_output.csv";
    private static final String OUTPUT_FILE_WITH_ROW_COUNT = "src/test/resources/test_output_with_row_count.csv";
    private static final int ROW_COUNT_INDEX = 0;
    private static final int PLATE_COLUMN = 0;
    private static final int WELL_INDEX = 1;
    private static final int DATA_INDEX = 2;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        deleteAndFlushFs(OUTPUT_FILE);
        deleteAndFlushFs(OUTPUT_FILE_WITH_ROW_COUNT);

        new SpringApplicationBuilder(Application.class).
                run(EXISTING_INPUT_FILE, OUTPUT_FILE);

        new SpringApplicationBuilder(Application.class).
                run(ApplicationUsage.INCLUDE_ROW_COUNT, EXISTING_INPUT_FILE, OUTPUT_FILE_WITH_ROW_COUNT);
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
    @Test
    public void headerOutput() throws Exception {

        List<List<Optional<String>>> sheet = new CsvParser().parse(OUTPUT_FILE);

        List<Optional<String>> header = sheet.get(0);
        assertThat(header.get(0).get(), equalTo("Plate"));
        assertThat(header.get(1).get(), equalTo("Well"));
        assertThat(header.get(2).get(), equalTo("Data"));
        assertThat(header, hasSize(3));
    }

    @Test
    public void sheetIsCorrectSize() throws Exception {
        List<List<Optional<String>>> sheet = new CsvParser().parse(OUTPUT_FILE);

        assertThat(sheet, hasSize(1153));
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
    @Test
    public void plateOutput() throws Exception {

        List<List<Optional<String>>> sheet = new CsvParser().parse(OUTPUT_FILE);

        for (int row = 1; row < 385; row++) {
            assertThat("row=" + row, sheet.get(row).get(PLATE_COLUMN).get(), equalTo("Plate1"));
        }

        for (int row = 386; row < 769; row++) {
            assertThat("row=" + row, sheet.get(row).get(PLATE_COLUMN).get(), equalTo("Plate2"));
        }

        for (int row = 770; row < 1153; row++) {
            assertThat("row=" + row, sheet.get(row).get(PLATE_COLUMN).get(), equalTo("Plate3"));
        }
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
    @Test
    public void plateOutputWithRowCount() throws Exception {

        List<List<Optional<String>>> sheet = new CsvParser().parse(OUTPUT_FILE_WITH_ROW_COUNT);

        assertThat(sheet.get(0).get(ROW_COUNT_INDEX).get(), equalTo("Row Count"));
        assertThat(sheet.get(1).get(ROW_COUNT_INDEX).get(), equalTo("1"));
        assertThat(sheet.get(1152).get(ROW_COUNT_INDEX).get(), equalTo("1152"));
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
    @Test
    public void wellOutput() throws Exception {

        List<List<Optional<String>>> sheet = new CsvParser().parse(OUTPUT_FILE);

        assertThat(sheet.get(0).get(WELL_INDEX).get(), equalTo("Well"));
        assertThat(sheet.get(1).get(WELL_INDEX).get(), equalTo("A01"));
        assertThat(sheet.get(2).get(WELL_INDEX).get(), equalTo("B01"));
        assertThat(sheet.get(17).get(WELL_INDEX).get(), equalTo("A02"));
        assertThat(sheet.get(32).get(WELL_INDEX).get(), equalTo("P02"));
        assertThat(sheet.get(384).get(WELL_INDEX).get(), equalTo("P24"));
        assertThat(sheet.get(385).get(WELL_INDEX).get(), equalTo("A01"));
        assertThat(sheet.get(1152).get(WELL_INDEX).get(), equalTo("P24"));
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "ConstantConditions"})
    @Test
    public void dataOutput() throws Exception {
        List<List<Optional<String>>> outputSheet = new CsvParser().parse(OUTPUT_FILE);

        assertThat(outputSheet.get(1).get(DATA_INDEX).get(), equalTo("4 2"));
        assertThat(outputSheet.get(2).get(DATA_INDEX).get(), equalTo("5 2"));
        assertThat(outputSheet.get(17).get(DATA_INDEX).get(), equalTo("4 3"));
        assertThat(outputSheet.get(18).get(DATA_INDEX).get(), equalTo("5 3"));
        assertThat(outputSheet.get(384).get(DATA_INDEX).get(), equalTo("19 25"));
        assertThat(outputSheet.get(385).get(DATA_INDEX).get(), equalTo("23 2"));
        assertThat(outputSheet.get(386).get(DATA_INDEX).get(), equalTo("24 2"));
        assertThat(outputSheet.get(768).get(DATA_INDEX).get(), equalTo("38 25"));
        assertThat(outputSheet.get(769).get(DATA_INDEX).get(), equalTo("42 2"));
        assertThat(outputSheet.get(1152).get(DATA_INDEX).get(), equalTo("57 25"));
    }
}
