
package us.mcfarlen.data;

/**
 * Transform Data.
 * Either F or T should be Data.
 * @param <F> Source Type
 * @param <T> Destination type Type
 */
public interface DataTransform<F,T> {
   T transform(F source);
}
