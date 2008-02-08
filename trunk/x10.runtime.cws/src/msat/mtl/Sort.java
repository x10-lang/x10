package msat.mtl;
/******************************************************************************************[Sort.h]
MiniSat -- Copyright (c) 2003-2006, Niklas Een, Niklas Sorensson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **************************************************************************************************/

public class Sort<T extends Comparator<T>> {
	interface LessThan<T> { 
		boolean apply(T x, T y);
	}
	LessThan<T> LT_DEFAULT = new LessThan<T>() {
		public boolean apply(T x, T y) {
			return x.lt(y);
		}
	};


	void selectionSort(T[] array, int size, LessThan<T> lt)
	{
		int     i, j, best_i;
		T       tmp;

		for (i = 0; i < size-1; i++){
			best_i = i;
			for (j = i+1; j < size; j++){
				if (lt.apply(array[j], array[best_i]))
					best_i = j;
			}
			tmp = array[i]; array[i] = array[best_i]; array[best_i] = tmp;
		}
	}
	void selectionSort(T[] array, int size) {
		selectionSort(array, size, LT_DEFAULT); 
	}


	void sort(T[] array, int base, int size, LessThan lt)
	{
		assert base+size < array.length;
		if (size <= 15)
			selectionSort(array, size, lt);

		else{
			T           pivot = array[base+ size / 2];
			T           tmp;
			int         i = -1;
			int         j = size;

			for(;;){
				do i++; while(lt.apply(array[base+ i], pivot));
				do j--; while(lt.apply(pivot, array[base+j]));

				if (i >= j) break;

				tmp = array[base+ i]; array[base+i] = array[base+j]; array[base+j] = tmp;
			}

			sort(array    , base, i     , lt);
			sort(array, base+i, size-i, lt);
		}
	}
	void sort(T[] array, int size, LessThan lt) {
		sort(array, 0, size, lt);
	}
	void sort(T[] array, int size) { 
		sort(array, 0, size, LT_DEFAULT);
	}

	void sort(Vec<T> v, LessThan lt) {
		sort(v.data, v.size(), lt);
		// TODO: the array is sorted in place, right?
	}
	void sort(Vec<T> v) {
		sort(v.data, v.size(), LT_DEFAULT);
		// TODO: the array is sorted in place, right?
	}

}

