
package io.nsingla.annotations;

public abstract class ExportDeserializer<T> {
    public ExportDeserializer() {
    }

    public abstract T deserialize(Object valueToParse);

    public abstract static class None extends ExportDeserializer<Object> {
        private None() {
        }
    }
}
