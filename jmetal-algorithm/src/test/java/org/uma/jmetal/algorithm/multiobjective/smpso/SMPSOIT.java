package org.uma.jmetal.algorithm.multiobjective.smpso;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class SMPSOIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() throws Exception {
    DoubleProblem problem = new ZDT4() ;

    algorithm = new SMPSOBuilder(problem, new CrowdingDistanceArchive<DoubleSolution>(100)).build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute();

    List<DoubleSolution> population = algorithm.getResult();

    /*
    Rationale: the default problem is ZDT4, and SMPSO, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(population.size() >= 98) ;
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    DoubleProblem problem = new ZDT4() ;

    algorithm = new SMPSOBuilder(problem, new CrowdingDistanceArchive<DoubleSolution>(100)).build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute();

    List<DoubleSolution> population = algorithm.getResult();

    QualityIndicator hypervolume = new Hypervolume("/referenceFronts/ZDT4.pf") ;

    // Rationale: the default problem is ZDT4, and SMPSO, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.64

    double hv = (Double)hypervolume.evaluate(population) ;

    assertTrue(hv > 0.64) ;
  }
}
