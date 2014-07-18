//  RunnerC.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Francisco Luna <fluna@unex.es>
//
//  Copyright (c) 2013 Antonio J. Nebro, Francisco Luna
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.core.Algorithm;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.experiment.Settings;
import org.uma.jmetal.experiment.SettingsFactory;
import org.uma.jmetal.qualityIndicator.QualityIndicator;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileOutput.DefaultFileOutputContext;
import org.uma.jmetal.util.fileOutput.FileOutputContext;
import org.uma.jmetal.util.fileOutput.SolutionSetOutput;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for running algorithms reading the configuration from properties files
 */
public class RunnerC {
  private static Logger logger_;
  private static FileHandler fileHandler_;

  /**
   * @param args Command line arguments.
   * @throws org.uma.jmetal.util.JMetalException
   * @throws java.io.IOException
   * @throws SecurityException       Usage: three options
   *                                 - org.uma.jmetal.experiment.Main algorithmName
   *                                 - org.uma.jmetal.experiment.Main algorithmName problemName
   *                                 - org.uma.jmetal.experiment.Main algorithmName problemName paretoFrontFile
   * @throws ClassNotFoundException
   */
  public static void main(String[] args) throws
    JMetalException, SecurityException, IOException,
    IllegalArgumentException, IllegalAccessException,
    ClassNotFoundException {
    Algorithm algorithm;

    QualityIndicator indicators;

    logger_ = Configuration.logger;
    fileHandler_ = new FileHandler("jMetal.log");
    logger_.addHandler(fileHandler_);

    Settings settings = null;

    String algorithmName = "";
    String problemName = "Kursawe";
    String paretoFrontFile = "";

    indicators = null;

    Properties configuration = new Properties();
    InputStreamReader inputStreamReader = null;

    if (args.length == 0) {
      logger_.log(Level.SEVERE, "Sintax error. Usage:");
      logger_.log(Level.SEVERE, "a) org.uma.jmetal.experiment.Main configurationFile ");
      logger_.log(Level.SEVERE, "b) org.uma.jmetal.experiment.Main configurationFile problemName");
      logger_.log(Level.SEVERE, "c) org.uma.jmetal.experiment.Main configurationFile problemName paretoFrontFile");
      throw new RuntimeException("Sintax error when invoking the program");
    } else if (args.length == 1) {
      inputStreamReader = new InputStreamReader(new FileInputStream(args[0]));
      configuration.load(inputStreamReader);

      algorithmName = configuration.getProperty("algorithm");
      Object[] settingsParams = {problemName};
      settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams);
    } else if (args.length == 2) {
      inputStreamReader = new InputStreamReader(new FileInputStream(args[0]));
      configuration.load(inputStreamReader);
      algorithmName = configuration.getProperty("algorithm");

      problemName = args[1];
      Object[] settingsParams = {problemName};
      settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams);
    } else if (args.length == 3) {
      inputStreamReader = new InputStreamReader(new FileInputStream(args[0]));
      configuration.load(inputStreamReader);
      algorithmName = configuration.getProperty("algorithm");

      problemName = args[1];
      paretoFrontFile = args[2];
      Object[] settingsParams = {problemName};
      settings = (new SettingsFactory()).getSettingsObject(algorithmName, settingsParams);
    }

    algorithm = settings.configure(configuration);
    inputStreamReader.close();

    if (args.length == 3) {
      Problem p = algorithm.getProblem();
      indicators = new QualityIndicator(p, paretoFrontFile);
    }

    // Execute the Algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;

    // Result messages
    logger_.info("Total execution time: " + estimatedTime + "ms");

    FileOutputContext fileContext = new DefaultFileOutputContext("VAR.tsv") ;
    fileContext.setSeparator("\t");
    logger_.info("Variables values have been writen to file VAR.tsv");
    SolutionSetOutput.printVariablesToFile(fileContext, population) ;

    fileContext = new DefaultFileOutputContext("FUN.tsv");
    fileContext.setSeparator("\t");
    SolutionSetOutput.printObjectivesToFile(fileContext, population);
    logger_.info("Objectives values have been written to file FUN");

    if (indicators != null) {
      logger_.info("Quality indicators");
      logger_.info("Hypervolume: " + indicators.getHypervolume(population));
      logger_.info("GD         : " + indicators.getGD(population));
      logger_.info("IGD        : " + indicators.getIGD(population));
      logger_.info("Spread     : " + indicators.getSpread(population));
      logger_.info("Epsilon    : " + indicators.getEpsilon(population));
    }
  }
}