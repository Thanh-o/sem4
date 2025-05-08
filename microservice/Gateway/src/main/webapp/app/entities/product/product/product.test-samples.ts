import { IProduct, NewProduct } from './product.model';

export const sampleWithRequiredData: IProduct = {
  id: 11737,
  name: 'hm sleepily',
  price: 17510.56,
};

export const sampleWithPartialData: IProduct = {
  id: 22728,
  name: 'sore ack unsteady',
  price: 6707.04,
  category: 'how',
};

export const sampleWithFullData: IProduct = {
  id: 4403,
  name: 'cafe',
  price: 21233.3,
  description: 'pillow',
  category: 'yuck',
};

export const sampleWithNewData: NewProduct = {
  name: 'anti inject why',
  price: 26562.23,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
