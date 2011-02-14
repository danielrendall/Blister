/*
 * Copyright 2011 Daniel Rendall
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.sromo.blister;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author Daniel Rendall
 */
public class App {

    enum Mode { NONE, ENCODE, DECODE;}

    @Option(name = "-m", usage = "mode", required = true)
    private Mode mode = Mode.NONE;

    @Option(name = "-i", usage = "input", required = true)
    private File input;

    @Option(name = "-o", usage = "output", required = true)
    private File output;

    public static void main(String[] args) {
        new App().doMain(args);
    }

    private void doMain(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);

        parser.setUsageWidth(160);

        try {
            // parse the arguments.
            parser.parseArgument(args);
            if (!input.exists()) {
                throw new CmdLineException("Input file didn't exist");
            }
            switch (mode) {

                case NONE:
                    throw new CmdLineException("Mode not specified");
                case ENCODE:
                    throw new CmdLineException("Encoding not supported yet");
                case DECODE:
                    byte[] bytes = FileUtils.readFileToByteArray(input);
                    BPItem root = BinaryPlist.decode(bytes);
                    String result = BinaryPlist.dump(root);
                    FileUtils.writeStringToFile(output, result);
                    break;
            }


        } catch (CmdLineException e) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            System.err.println("java App [options...] arguments...");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
