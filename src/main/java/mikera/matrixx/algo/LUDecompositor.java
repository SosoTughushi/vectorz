package mikera.matrixx.algo;

import mikera.vectorz.Vector;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;

/*
 * Copyright 2011-2013, by Vladimir Kostyukov, Mike Anderson and Contributors.
 * 
 * This file is adapted from the la4j project (http://la4j.org)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributor(s): -
 * 
 */

// TODO: implement pivoting
public class LUDecompositor {

	public static Matrix[] decompose(AMatrix matrix) {
		return decomposeInternal(Matrix.create(matrix));
	}

	public static Matrix[] decompose(Matrix matrix) {
		return decomposeInternal(matrix.clone());
	}

	private static Matrix[] decomposeInternal(Matrix lu) {
		if (!lu.isSquare()) { throw new IllegalArgumentException(
				"Wrong matrix size: " + "not square"); }

		int n = lu.rowCount();

		for (int j = 0; j < n; j++) {

			Vector jcolumn = lu.getColumn(j).toVector();

			for (int i = 0; i < n; i++) {

				int kmax = Math.min(i, j);

				double s = 0.0;
				for (int k = 0; k < kmax; k++) {
					s += lu.get(i, k) * jcolumn.get(k);
				}

				jcolumn.set(i, jcolumn.get(i) - s);
				lu.set(i, j, jcolumn.get(i));
			}

			int p = j;

			for (int i = j + 1; i < n; i++) {
				if (Math.abs(jcolumn.get(i)) > Math.abs(jcolumn.get(p))) p = i;
			}

			if (p != j) {
				// this seems to be a bug in original LU code???
				//lu.swapRows(p, j);
			}

			if ((j < n) & lu.get(j, j) != 0.0) {
				for (int i = j + 1; i < n; i++) {
					lu.set(i, j, lu.get(i, j) / lu.get(j, j));
				}
			}
		}

		Matrix l = Matrix.create(n, n);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < i; j++) {
				l.set(i, j, lu.get(i, j));
			}
			l.set(i, i, 1.0);
		}

		// clear low elements to ensure upper triangle only is populated		
		Matrix u=lu;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < i; j++) {
				u.set(i, j, 0.0);
			}
		}

		return new Matrix[] { l, u };
	}
}