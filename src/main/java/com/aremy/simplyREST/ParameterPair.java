package com.aremy.simplyREST;

public class ParameterPair {
        private final String name;
        private final String value;

        public ParameterPair(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() { return name; }
        public String getValue() { return value; }

        @Override
        public int hashCode() { return name.hashCode() ^ value.hashCode(); }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ParameterPair)) return false;
            ParameterPair pairo = (ParameterPair) o;
            return this.name.equals(pairo.getName()) &&
                    this.value.equals(pairo.getValue());
        }

}
