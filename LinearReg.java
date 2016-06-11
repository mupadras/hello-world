/**
 * Created by madhu on 6/11/16.
 */
import http://math.nist.gov/javanumerics/jama/;
import Jama.Matrix;
        import java.io.*;

        import java.util.*;


/**
 * A class to train and evaluate Linear Regression models with L2-Regularization
 
public class LinearReg
{
    public Double lambda;
    public String trainingFile;
    public String testingFile;

    public LinearReg() {
        lambda = 1.0;
        trainingFile = "";
        testingFile = "";
    }

    /**
     * Read a matrix from a comma sperated file
     *
     * @param fileName
     * @return
     */
    private Matrix readMatrix(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            List<double[]> data_array = new ArrayList<double[]>();

            String line;
            while ((line = reader.readLine()) != null) {
                String fields[] = line.split(",");
                double data[] = new double[fields.length];
                for (int i = 0; i < fields.length; ++i) {
                    data[i] = Double.parseDouble(fields[i]);
                }
                data_array.add(data);
            }

            if (data_array.size() > 0) {
                int cols = data_array.get(0).length;
                int rows = data_array.size();
                Matrix matrix = new Matrix(rows, cols);
                for (int r = 0; r < rows; ++r) {
                    for (int c = 0; c < cols; ++c) {
                        matrix.set(r, c, data_array.get(r)[c]);
                    }
                }
                return matrix;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return new Matrix(0, 0);
    }

    /**
     * the last column correspond to the target
     * Hence, remove target values from the last column of a data set.
     * <p>
     * Meanwhile, we need to add 1 to 0 column of each row as a bias term
     *
     * @param data_set
     * @return
     */
    private Matrix getDataPoints(Matrix data_set) {
        Matrix features = data_set.getMatrix(0, data_set.getRowDimension() - 1, 0, data_set.getColumnDimension() - 2);
        int rows = features.getRowDimension();
        int cols = features.getColumnDimension() + 1;
        Matrix modifiedFeatures = new Matrix(rows, cols);
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                if (c == 0) {
                    modifiedFeatures.set(r, c, 1.0);
                } else {
                    modifiedFeatures.set(r, c, features.get(r, c-1));
                }
            }
        }
        return modifiedFeatures;
    }

    /**
     * Returns the target values from the last column of a data set.
     *
     * @param data_set
     * @return
     */
    private Matrix getTargets(Matrix data_set) {
        return data_set.getMatrix(0, data_set.getRowDimension() - 1, data_set.getColumnDimension() - 1, data_set.getColumnDimension() - 1);
    }

    /**
     * train the model using linear regression with L2 regularizer
     * <p>
     * The first column of the features should be set as all 1. because the first feature should
     * perform as bias
     * <p>
     * Linear regression with L2 regularizer has the close form as follows:
     * w = (x^{T} * X + \lambda * I)^{-1} * X^{T} * t
     *
     * @param data n * m
     * @param targets n * 1
     * @param lambda
     * @return weight m * 1
     */
    private Matrix trainLinearRegressionModel(Matrix data, Matrix targets, Double lambda) {
        int row = data.getRowDimension();
        int column = data.getColumnDimension();
        Matrix identity = Matrix.identity(column, column);
        identity.times(lambda);
        Matrix dataCopy = data.copy();
        Matrix transponseData = dataCopy.transpose();
        Matrix norm = transponseData.times(data);
        Matrix circular = norm.plus(identity);
        Matrix circularInverse = circular.inverse();
        Matrix former = circularInverse.times(data.transpose());
        Matrix weight = former.times(targets);

        return weight;
    }

    /**
     * test the model using the weights trained using linear regression with L2 regularizer
     *
     * @param data n * m matrix
     * @param targets n * 1 matrix
     * @param weights m * 1 matrix
     * @return
     */
    private double evaluateLinearRegressionModel(Matrix data, Matrix targets, Matrix weights) {
        double error = 0.0;
        int row = data.getRowDimension();
        int column = data.getColumnDimension();
        assert row == targets.getRowDimension();
        assert column == weights.getColumnDimension();

        Matrix predictTargets = predict(data, weights);
        for (int i = 0; i < row; i++) {
            error = (targets.get(i, 0) - predictTargets.get(i, 0)) * (targets.get(i, 0) - predictTargets.get(i, 0));
        }

        return 0.5 * error;
    }

    /**
     * calcualte the predict targests given features and the learned weights
     *
     * @param data a matrix with n * m
     * @param weights a matrix with 1 * m
     * @return predict targets according to the weight
     */
    private Matrix predict(Matrix data, Matrix weights) {
        int row = data.getRowDimension();
        Matrix predictTargets = new Matrix(row, 1);
        for (int i = 0; i < row; i++) {
            double value = multiply(data.getMatrix(i, i, 0, data.getColumnDimension() -1 ), weights);
            //System.out.println(value);
            predictTargets.set(i, 0, value);
        }
        return predictTargets;
    }

    /**
     * multiply two matrix with just 1 row and seveal columns
     *
     * @param data a matrix with 1 * column
     * @param weights a matrix with 1 * column
     * @return
     */
    private Double multiply(Matrix data, Matrix weights) {
        Double sum = 0.0;
        int column = data.getColumnDimension();
        for (int i = 0; i <column; i++) {
            sum += data.get(0, i) * weights.get(i, 0);
        }
        return sum;
    }

    /**
     * args consists of the training file path and testing file path
     * <p>
     * If you want to set the lamdab (penalization coefficient), you can go to the Linear Regression constructor
     * to update the value of lambda
     * <p>
     * In addition, you can update the training path and testing path in the same way.
     * <p>
     * <b>NOTE</b>Because we use the JAMA package, if you use the Eclipse to run the problem, then
     * you need to add the jar file into the library classPath.
     * Or if you just use javac and java to compile and run the program, remember that you also need to
     * add the classPath into the command line
     *
     * @param args
     */
    public static void main(String[] args) {
        LinearReg lr = new LinearReg();
        try {
            Matrix training = lr.readMatrix(lr.trainingFile);
            Matrix testing = lr.readMatrix(lr.testingFile);

            /** get the actual features, meanwhile add a N*1 column vector with value being all 1 as the first column of the features */
            Matrix trainingData = lr.getDataPoints(training);
            Matrix testingData = lr.getDataPoints(testing);

            Matrix trainingTargets = lr.getTargets(training);
            Matrix testingTargets = lr.getTargets(testing);

            // Train the model.
            Matrix weights = lr.trainLinearRegressionModel(trainingData, trainingTargets, lr.lambda);
            for (int i = 0; i < weights.getRowDimension(); i++) {
                System.out.println(weights.get(i, 0));
            }

            // Evaluate the model using training and testing data.
            double training_error = lr.evaluateLinearRegressionModel(trainingData, trainingTargets, weights);
            double testing_error = lr.evaluateLinearRegressionModel(testingData, testingTargets, weights);

            System.out.println(training_error);
            System.out.println(testing_error);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
