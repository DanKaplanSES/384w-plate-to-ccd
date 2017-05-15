package com.sleepeasysoftware.platetoccd;

import com.google.common.collect.Lists;
import com.sleepeasysoftware.platetoccd.model.Plate;
import com.sleepeasysoftware.platetoccd.parser.ExcelParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Optional;

import static com.sleepeasysoftware.platetoccd.ApplicationUsageTest.EXISTING_INPUT_FILE;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Created by Daniel Kaplan on behalf of Sleep Easy Software.
 */
public class DataToPlatesTest {

    static final String REARRANGED_COLUMNS = "src/test/resources/plate1_rearranged_column.xlsx";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DataToPlates subject = new DataToPlates();

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void plateDataFromInput() throws Exception {
        List<List<Optional<String>>> sheet = new ExcelParser().parseFirstSheet(EXISTING_INPUT_FILE);

        List<Plate> plates = subject.execute(sheet, Lists.newArrayList());

        assertThat(plates, hasSize(3));

        Plate plate1 = plates.get(0);
        assertThat(plate1.getName(), equalTo("Plate1"));
        assertThat(plate1.getData().get("A", "01").get(), equalTo("4 2"));
        assertThat(plate1.getData().get("P", "24").get(), equalTo("19 25"));
        assertThat(plate1.getData().columnKeySet().size(), equalTo(24));
        assertThat(plate1.getData().rowKeySet().size(), equalTo(16));

        Plate plate2 = plates.get(1);
        assertThat(plate2.getName(), equalTo("Plate2"));
        assertThat(plate2.getData().get("A", "01").get(), equalTo("23 2"));
        assertThat(plate2.getData().get("P", "24").get(), equalTo("38 25"));
        assertThat(plate2.getData().columnKeySet().size(), equalTo(24));
        assertThat(plate2.getData().rowKeySet().size(), equalTo(16));

        Plate plate3 = plates.get(2);
        assertThat(plate3.getName(), equalTo("Plate3"));
        assertThat(plate3.getData().get("A", "01").get(), equalTo("42 2"));
        assertThat(plate3.getData().get("P", "24").get(), equalTo("57 25"));
        assertThat(plate3.getData().columnKeySet().size(), equalTo(24));
        assertThat(plate3.getData().rowKeySet().size(), equalTo(16));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void respectColumnOrderWhenNotOrdered() throws Exception {
        List<List<Optional<String>>> sheet = new ExcelParser().parseFirstSheet(REARRANGED_COLUMNS);

        List<Plate> plates = subject.execute(sheet, Lists.newArrayList());

        assertThat(plates, hasSize(3));

        Plate plate1 = plates.get(0);
        assertThat(plate1.getName(), equalTo("Plate1"));
        assertThat(plate1.getData().get("A", "01").get(), equalTo("4 25"));
        assertThat(plate1.getData().get("P", "24").get(), equalTo("19 2"));
        assertThat(plate1.getData().columnKeySet().size(), equalTo(24));
        assertThat(plate1.getData().rowKeySet().size(), equalTo(16));

        Plate plate2 = plates.get(1);
        assertThat(plate2.getName(), equalTo("Plate2"));
        assertThat(plate2.getData().get("A", "01").get(), equalTo("23 2"));
        assertThat(plate2.getData().get("P", "24").get(), equalTo("38 25"));
        assertThat(plate2.getData().columnKeySet().size(), equalTo(24));
        assertThat(plate2.getData().rowKeySet().size(), equalTo(16));
    }
}