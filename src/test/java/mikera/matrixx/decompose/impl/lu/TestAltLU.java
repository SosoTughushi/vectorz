package mikera.matrixx.decompose.impl.lu;

import static org.junit.Assert.*;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.decompose.ILUPResult;
import mikera.matrixx.decompose.impl.lu.AltLU;
import mikera.matrixx.decompose.impl.lu.SimpleLUP;

import org.junit.Test;

public class TestAltLU {

  @Test
  public void testDecompose() {
//	  TEST: 1
	  double[][] dataA = {{5, 2, 3}, {1.5, -2, 8}, {-3, 4.7, -0.5}};
	  Matrix A = Matrix.create(dataA);
	  LUPResult ans = AltLU.decompose(A);
	  AMatrix L = ans.getL();
	  AMatrix U = ans.getU();
	  AMatrix P = ans.getP();
	  
	  double[][] exceptDataL = {{1, 0, 0}, {-0.6, 1, 0}, {0.3, -0.44068, 1}};
	  double[][] exceptDataU = {{5, 2, 3}, {0, 5.9, 1.3}, {0, 0, 7.67288}};
	  double[][] exceptDataP = {{1, 0, 0}, {0, 0, 1}, {0, 1, 0}};
	  Matrix exceptL = Matrix.create(exceptDataL);
	  Matrix exceptU = Matrix.create(exceptDataU);
	  Matrix exceptP = Matrix.create(exceptDataP);
	  assertArrayEquals(L.getElements(), exceptL.data, 1e-5);
	  assertArrayEquals(U.getElements(), exceptU.data, 1e-5);
	  assertArrayEquals(P.getElements(), exceptP.data, 1e-5);
	  assertTrue(Math.abs(-226.350 - ans.computeDeterminant()) < 1e-3);
	  
//	  TEST: 2
	  dataA = new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
	  Matrix B = Matrix.create(dataA);
	  ans = AltLU.decompose(B);
	  L = ans.getL();
	  U = ans.getU();
	  P = ans.getP();
	
	  exceptDataL = new double[][]{{1, 0, 0}, {0.142857, 1, 0}, {0.571429, 0.5, 1}};
	  exceptDataU = new double[][]{{7, 8, 9}, {0, 0.857143, 1.714286}, {0, 0, 0}};
	  exceptDataP = new double[][]{{0, 1, 0}, {0, 0, 1}, {1, 0, 0}};
	  exceptL = Matrix.create(exceptDataL);
	  exceptU = Matrix.create(exceptDataU);
	  exceptP = Matrix.create(exceptDataP);
	  assertArrayEquals(L.getElements(), exceptL.data, 1e-5);
	  assertArrayEquals(U.getElements(), exceptU.data, 1e-5);
	  assertArrayEquals(P.getElements(), exceptP.data, 1e-5);
	  assertTrue(Math.abs(0 - ans.computeDeterminant()) < 1e-3);
	    
//		AMatrix LU=L.innerProduct(U);
//		AMatrix PA=P.innerProduct(A);
// TODO: apprears to be broken? Needs fixing
//		if(!LU.epsilonEquals(PA)) {
//			fail("\n"+"L="+L+"\n"
//					+"U="+U+"\n"
//					+"P="+P+"\n"
//				  	+"A="+A+"\n"
//				  	+"LU="+LU+"\n"
//				  	+"PA="+PA+"\n");
//		}  
  }
  
// TODO: AltLU seems to be broken? Need to fix or remove
// @Test public void testRandomDecomposeAltLU() {
//	  AMatrix a=Matrixx.createRandomMatrix(4, 4);
//	  ILUPResult r=new AltLU(a);
//	  
//	  AMatrix l=r.getL();
//	  AMatrix u=r.getU();
//	  AMatrix p=r.getP();
//	  AMatrix lu=l.innerProduct(u);
//	  AMatrix pa=p.innerProduct(a);
//	  
//	  if(!lu.epsilonEquals(pa)) {
//		  fail("L="+l+"\n"
//				  +"U="+u+"\n"
//				  +"P="+p+"\n"
//				  +"A="+a+"\n"
//				  +"LU="+lu+"\n"
//				  +"PA="+pa+"\n");
//	  }
//  }

  @Test public void testRandomDecompose() {
	  AMatrix a=Matrixx.createRandomMatrix(4, 4);
	  ILUPResult r=SimpleLUP.decompose(a);
	  
	  AMatrix lu=r.getL().innerProduct(r.getU());
	  AMatrix pa=r.getP().innerProduct(a);
	  
	  if(!lu.epsilonEquals(pa)) {
		  fail("L="+r.getL()+"\n"+"U="+r.getU()+"\n"+"LU="+lu+"\n"+"PA="+pa+"\n");
	  }
  }
}