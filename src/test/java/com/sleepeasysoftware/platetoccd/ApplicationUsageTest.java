package com.sleepeasysoftware.platetoccd;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.File;

import static com.sleepeasysoftware.platetoccd.FileDelete.deleteAndFlushFs;

/**
 * Created by Daniel Kaplan on behalf of Sleep Easy Software.
 */
public class ApplicationUsageTest {

    static final String EXISTING_INPUT_FILE = "src/test/resources/happy_path_input.xlsx";
    private static final String EXISTING_OUTPUT_FILE = "src/test/resources/happy_path_input.xlsx";
    static final String DOES_NOT_EXIST_FILE = "src/test/resources/does_not_exist";

    private SpringApplicationBuilder subject;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        deleteAndFlushFs(DOES_NOT_EXIST_FILE);

        subject = new SpringApplicationBuilder(Application.class);
    }

    @Test
    public void requiresInput() throws Exception {

        thrown.expect(IllegalStateException.class);
        subject.run();
    }

    @Test
    public void happyPathHasNoErrors() throws Exception {

        subject.run(EXISTING_INPUT_FILE, DOES_NOT_EXIST_FILE);
    }

    @Test
    public void requireExistingInputFile() throws Exception {
        thrown.expect(IllegalStateException.class);

        subject.run(DOES_NOT_EXIST_FILE, DOES_NOT_EXIST_FILE);
    }

    @Test
    public void requireNonExistingOutputFile() throws Exception {
        thrown.expect(IllegalStateException.class);

        subject.run(EXISTING_INPUT_FILE, EXISTING_OUTPUT_FILE);
    }

    @Test
    public void acceptsIncludeRowCount() throws Exception {
        String includeRowCount = "--include-row-count";

        subject.run(includeRowCount, EXISTING_INPUT_FILE, DOES_NOT_EXIST_FILE);
        subject.run(EXISTING_INPUT_FILE, includeRowCount, DOES_NOT_EXIST_FILE);
        subject.run(EXISTING_INPUT_FILE, DOES_NOT_EXIST_FILE, includeRowCount);
    }
}
