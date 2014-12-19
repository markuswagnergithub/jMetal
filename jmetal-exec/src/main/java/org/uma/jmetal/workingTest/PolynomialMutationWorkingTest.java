package org.uma.jmetal.workingTest;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.solution.DoubleSolution;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class is intended to verify the working of the polynomial mutation operator. A figure
 * depicting the values obtained when generating 10000 points, a granularity of 100, and a number
 * of different distribution index values (5.0, 10, 20, 40) can be found here:
 * <a href="https://github.com/jMetal/jMetal/blob/master/figures/polynomial.png">
   https://github.com/jMetal/jMetal/blob/master/figures/polynomial.png</a>
 */
public class PolynomialMutationWorkingTest {
  /**
   * Program to generate data representing the distribution of points generated by a polynomial
   * mutation operator. The parameters to be introduced by the command line are:
   * - numberOfSolutions: number of solutions to generate
   * - granularity: number of subdivisions to be considered.
   * - distributionIndex: distribution index of the polynomial mutation operator
   * - outputFile: file containing the results
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) throws FileNotFoundException {
    if (args.length !=4) {
      System.err.println("Usage: numberOfSolutions granularity distributionIndex outputFile") ;
      System.exit(-1) ;
    }
    int numberOfPoints = Integer.valueOf(args[0]) ;
    int granularity = Integer.valueOf(args[1]) ;
    double distributionIndex = Double.valueOf(args[2]) ;
    String outputFileName = args[3] ;
    DoubleProblem problem ;

    problem = new Sphere(1) ;
    MutationOperator mutation = new PolynomialMutation(1.0, distributionIndex) ;

    DoubleSolution solution = problem.createSolution() ;
    solution.setVariableValue(0, 0.0);

    List<DoubleSolution> population = new ArrayList<>(numberOfPoints) ;
    for (int i = 0 ; i < numberOfPoints ; i++) {
      DoubleSolution newSolution = (DoubleSolution) solution.copy();
      mutation.execute(newSolution) ;
      population.add(newSolution) ;
    }

    Collections.sort(population, new VariableComparator()) ;
    double[][] classifier = classify(population, problem, granularity);


    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName)));

    try {
      for (int i = 0; i < classifier.length; i++) {
        bufferedWriter
            .write(classifier[i][0] + "\t" + classifier[i][1]);
        bufferedWriter.newLine();
      }
      bufferedWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static double[][] classify(List<DoubleSolution> solutions, DoubleProblem problem, int granularity) {
    double grain = (problem.getUpperBound(0) - problem.getLowerBound(0)) / granularity ;
    double[][] classifier = new double[granularity][] ;
    for (int i = 0 ; i < granularity; i++) {
      classifier[i] = new double[2] ;
      classifier[i][0] = problem.getLowerBound(0) + i * grain ;
      classifier[i][1] = 0 ;
    }

    for (DoubleSolution solution : solutions) {
      boolean found = false ;
      int index = 0 ;
      while (!found) {
        if (solution.getVariableValue(0) <= classifier[index][0]) {
          classifier[index][1] ++ ;
          found = true ;
        } else {
          if (index == (granularity - 1)) {
            classifier[index][1] ++ ;
            found = true ;
          } else {
            index++;
          }
        }
      }
    }

    return classifier ;
  }

  public static class VariableComparator implements Comparator<DoubleSolution> {
    /**
     * Compares two solutions according to the first variable value
     *
     * @param solution1 Object representing the first <code>Solution</code>.
     * @param solution2 Object representing the second <code>Solution</code>.
     * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
     * respectively.
     */
    @Override
    public int compare(DoubleSolution solution1, DoubleSolution solution2) {
      if (solution1 == null) {
        return 1;
      } else if (solution2 == null) {
        return -1;
      }

      if (solution1.getVariableValue(0) < solution2.getVariableValue(0)) {
        return -1;
      }

      if (solution1.getVariableValue(0) > solution2.getVariableValue(0)) {
        return 1;
      }

      return 0;
    }
  }
}
