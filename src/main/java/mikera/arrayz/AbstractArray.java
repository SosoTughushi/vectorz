package mikera.arrayz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mikera.vectorz.AScalar;
import mikera.vectorz.Ops;
import mikera.vectorz.Tools;
import mikera.vectorz.util.VectorzException;

/**
 * Abstract base class for INDArray implementations
 * @author Mike
 * @param <T> The type of array slices
 */
public abstract class AbstractArray<T> implements INDArray, Iterable<T> {

	public double get() {
		return get(new int[0]);
	}
	public double get(int x) {
		return get(new int[] {x});
	}
	public double get(int x, int y) {
		return get(new int[] {x,y});
	}
	
	@Override
	public int getShape(int dim) {
		return getShape()[dim];
	}
	
	public INDArray innerProduct(INDArray a) {
		if (a instanceof AScalar) {
			INDArray c=clone();
			c.scale(((AScalar)a).get());
			return c;
		}
		throw new UnsupportedOperationException();
	}
	
	public INDArray outerProduct(INDArray a) {
		ArrayList<INDArray> al=new ArrayList<INDArray>();
		for (Object s:this) {
			if (s instanceof INDArray) {
				al.add(((INDArray)s).outerProduct(a));
			} else {
				double x=Tools.toDouble(s);
				INDArray sa=a.clone();
				sa.scale(x);
				al.add(sa);
			}
		}
		return Arrayz.create(al);
	}
	
	public final void scale(double d) {
		multiply(d);
	}

	public void set(double value) {
		set(new int[0],value);
	}
	public void set(int x, double value) {
		set(new int[] {x},value);
	}
	public void set(int x, int y, double value) {
		set(new int[] {x,y},value);	
	}
	public void set (INDArray a) {
		int tdims=this.dimensionality();
		int adims=a.dimensionality();
		if (adims<tdims) {
			int sc=getShape()[0];
			for (int i=0; i<sc; i++) {
				INDArray s=slice(i);
				s.set(a);
			}
		} else if (adims==tdims) {
			int sc=sliceCount();
			for (int i=0; i<sc; i++) {
				INDArray s=slice(i);
				s.set(a.slice(i));
			}
		} else {
			throw new IllegalArgumentException("Can't set array to value of higher dimensionality");
		}
	}
	
	public void set(Object o) {
		if (o instanceof INDArray) {set((INDArray)o); return;}
		if (o instanceof Number) {
			set(((Number)o).doubleValue()); return;
		}
		if (o instanceof Iterable<?>) {
			int i=0;
			for (Object ob: ((Iterable<?>)o)) {
				slice(i).set(ob);
			}
			return;
		}
		if (o instanceof double[]) { 
			setElements((double[])o);
			return;
		}
		throw new UnsupportedOperationException("Can't set to value for "+o.getClass().toString());		
	}
	
	public void setElements(double[] values) {
		setElements(values,0,values.length);
	}
	
	public void square() {
		applyOp(Ops.SQUARE);
	}
	
	@Override
	public Iterator<T> iterator() {
		return new SliceIterator<T>(this);
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof INDArray)) return false;
		return equals((INDArray)o);
	}

	@Override
	public int hashCode() {
		return asVector().hashCode();
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		int length=sliceCount();
		sb.append('[');
		if (length>0) {
			sb.append(slice(0).toString());
			for (int i = 1; i < length; i++) {
				sb.append(',');
				sb.append(slice(i).toString());
			}
		}
		sb.append(']');
		return sb.toString();
	}
	
	public AbstractArray<?> clone() {
		try {
			return (AbstractArray<?>)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new VectorzException("AbstractArray clone failed");
		}
	}
	
	@Override
	public void add(INDArray a) {
		int n=sliceCount();
		int na=a.sliceCount();
		int dims=dimensionality();
		int adims=a.dimensionality();
		if (dims==adims) {
			if (n!=na) throw new VectorzException("Non-matching dimensions");
			for (int i=0; i<n; i++) {
				slice(i).add(a.slice(i));
			}
		} else if (adims<dims) {
			for (int i=0; i<n; i++) {
				slice(i).add(a);
			}	
		} else {
			throw new VectorzException("Cannot add array of greater dimensionality");
		}
	}
	
	@Override
	public long nonZeroCount() {
		long result=0;
		int n=sliceCount();
		for (int i=0; i<n; i++) {
			result+=slice(i).nonZeroCount();
		}
		return result;
	}
	
	@Override
	public double elementSum() {
		double result=0;
		int n=sliceCount();
		for (int i=0; i<n; i++) {
			result+=slice(i).elementSum();
		}
		return result;
	}


	@Override
	public void sub(INDArray a) {
		int n=sliceCount();
		int na=a.sliceCount();
		int dims=dimensionality();
		int adims=a.dimensionality();
		if (dims==adims) {
			if (n!=na) throw new VectorzException("Non-matching dimensions");
			for (int i=0; i<n; i++) {
				slice(i).sub(a.slice(i));
			}
		} else if (adims<dims) {
			for (int i=0; i<n; i++) {
				slice(i).sub(a);
			}	
		} else {
			throw new VectorzException("Cannot add array of greater dimensionality");
		}	
	}
	
	@Override
	public INDArray reshape(int... targetShape) {
		return Arrayz.createFromVector(asVector(), targetShape);
	}
	
	@Override
	public abstract List<T> getSlices();
	
	@Override
	public void getElements(double[] dest, int offset) {
		int sc=sliceCount();
		for (int i=0; i<sc; i++) {
			INDArray s=slice(i);
			s.getElements(dest,offset);
			offset+=s.elementCount();
		}
	}
	
	@Override
	public void copyTo(double[] arr) {
		getElements(arr,0);
	}
	
	@Override
	public INDArray broadcast(int... targetShape) {
		int dims=dimensionality();
		int tdims=targetShape.length;
		if (tdims<dims) {
			throw new VectorzException("Can't broadcast to a smaller shape!");
		} else if (dims==tdims) {
			return this;
		} else {
			int n=targetShape[0];
			INDArray s=broadcast(Arrays.copyOfRange(targetShape, 1, tdims));
			return SliceArray.repeat(s,n);
		}
	}
	
	@Override
	public void validate() {
		// TODO: any generic validation?
	}
}
