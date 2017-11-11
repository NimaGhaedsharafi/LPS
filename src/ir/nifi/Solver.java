package ir.nifi;

import ilog.concert.*;
import ilog.cplex.CpxLinearIntExpr;
import ilog.cplex.CpxNumVar;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.List;

/**
 * Generalized Solver for Linear Programming
 */
public class Solver {

    private final int variables;
    private final int constraint;
    private final double[] cost;
    private final double[][] coefficients;
    private final double[][] demands;
    private final boolean isMinimization;

    /**
     * @param variables    Number of decision variables
     * @param constraints  Number of constraints
     * @param cost         Cost Vector
     * @param coefficients Constraint coefficient matrix
     * @param demands      Demand vector
     */
    public Solver(int variables, int constraints, double[] cost, double[][] coefficients, double[][] demands, boolean isMinimization) {

        this.variables = variables;
        this.constraint = constraints;
        this.cost = cost;
        this.coefficients = coefficients;
        this.demands = demands;
        this.isMinimization = isMinimization;
    }

    /**
     *
     */
    public Result solve() throws IloException {
        // Instantiate an empty model
        IloCplex model = new IloCplex();

        // Define an array of decision variables
        IloNumVar[] variables = new IloNumVar[this.variables];
        for (int i = 0; i < this.variables; i++) {
            // Define each variable's range from 0 to +Infinity
            variables[i] = model.numVar(0, Double.MAX_VALUE);
        }

        // Define the problem function
        IloLinearNumExpr problem = model.linearNumExpr();
        // Add expressions for problem
        for (int i = 0; i < this.variables; i++) {
            problem.addTerm(this.cost[i], variables[i]);
        }
        if (this.isMinimization) {
            model.addMinimize(problem);
        } else {
            model.addMaximize(problem);
        }

        // Create a list of constraints
        List<IloRange> constraints = new ArrayList<>();

        for (int i = 0; i < this.constraint; i++) { // for each constraint
            IloLinearNumExpr constraint = model.linearNumExpr();
            for (int j = 0; j < this.variables; j++) { // for each variable
                constraint.addTerm(this.coefficients[i][j], variables[j]);
            }
            constraints.add(model.addGe(constraint, demands[i]));
        }
        // Suppress the auxiliary output printout
        model.setParam(IloCplex.IntParam.SimDisplay, 0);

        if (model.solve()) {
            return this.createResponse(model, constraints);
        }
        return null;
    }

    private Result createResponse(IloCplex model, List<IloRange> constraints) {
        return null;
    }
}
