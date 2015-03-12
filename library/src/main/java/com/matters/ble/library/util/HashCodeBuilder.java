package com.matters.ble.library.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import java.util.Set;

/**
 * HashCodeBuilder is used to build hashCode for objects. It was copied from commons-IO open source project.
 * For more information
 * @see <a href="http://commons.apache.org/proper/commons-io/">Apache commons IO project</a>
 */
public final class HashCodeBuilder {
    private static final ThreadLocal<Set<IDKey>> REGISTRY = new ThreadLocal<Set<IDKey>>();

    /*
     * NOTE: we cannot store the actual objects in a HashSet, as that would use the very hashCode()
     * we are in the process of calculating.
     *
     * So we generate a one-to-one mapping from the original object to a new object.
     *
     * Now HashSet uses equals() to determine if two elements with the same hashcode really
     * are equal, so we also need to ensure that the replacement objects are only equal
     * if the original objects are identical.
     *
     * The original implementation (2.4 and before) used the System.indentityHashCode()
     * method - however this is not guaranteed to generate unique ids (e.g. LANG-459)
     *
     * We now use the IDKey helper class (adapted from org.apache.axis.utils.IDKey)
     * to disambiguate the duplicate ids.
     */

    /**
     * <p>
     * Returns the registry of objects being traversed by the reflection methods in the current thread.
     * </p>
     *
     * @return Set the registry of objects being traversed
     * @since 2.3
     */
    static Set<IDKey> getRegistry() {
        return REGISTRY.get();
    }

    /**
     * <p>
     * This method uses reflection to build a valid hash code.
     * </p>
     *
     * <p>
     * It uses <code>AccessibleObject.setAccessible</code> to gain access to private fields. This means that it will
     * throw a security exception if run under a security manager, if the permissions are not set up correctly. It is
     * also not as efficient as testing explicitly.
     * </p>
     *
     * <p>
     * If the TestTransients parameter is set to <code>true</code>, transient members will be tested, otherwise they
     * are ignored, as they are likely derived fields, and not part of the value of the <code>Object</code>.
     * </p>
     *
     * <p>
     * Static fields will not be tested. Superclass fields will be included.
     * </p>
     *
     * <p>
     * Two randomly chosen, non-zero, odd numbers must be passed in. Ideally these should be different for each class,
     * however this is not vital. Prime numbers are preferred, especially for the multiplier.
     * </p>
     *
     * @param initialNonZeroOddNumber
     *            a non-zero, odd number used as the initial value
     * @param multiplierNonZeroOddNumber
     *            a non-zero, odd number used as the multiplier
     * @param object
     *            the Object to create a <code>hashCode</code> for
     * @param testTransients
     *            whether to include transient fields
     * @return int hash code
     * @throws IllegalArgumentException
     *             if the Object is <code>null</code>
     * @throws IllegalArgumentException
     *             if the number is zero or even
     */

    /**
     * Constant to use in building the hashCode.
     */
    private final int iConstant;

    /**
     * Running total of the hashCode.
     */
    private int iTotal = 0;

    /**
     * Init hash code builder.
     *
     * @return the hash code builder
     */
    public static HashCodeBuilder init() {
        return new HashCodeBuilder();
    }

    /**
     * Init hash code builder.
     *
     * @param initialOddNumber the initial odd number
     * @param multiplierOddNumber the multiplier odd number
     * @return the hash code builder
     */
    public static HashCodeBuilder init(final int initialOddNumber, final int multiplierOddNumber) {
        return new HashCodeBuilder(initialOddNumber, multiplierOddNumber);
    }

    /**
     * <p>
     * Uses two hard coded choices for the constants needed to build a <code>hashCode</code>.
     * </p>
     */
    private HashCodeBuilder() {
        iConstant = 37;
        iTotal = 17;
    }

    /**
     * <p>
     * Two randomly chosen, odd numbers must be passed in. Ideally these should be different for each class,
     * however this is not vital.
     * </p>
     *
     * <p>
     * Prime numbers are preferred, especially for the multiplier.
     * </p>
     *
     * @param initialOddNumber
     *            an odd number used as the initial value
     * @param multiplierOddNumber
     *            an odd number used as the multiplier
     * @throws IllegalArgumentException
     *             if the number is even
     */
    private HashCodeBuilder(final int initialOddNumber, final int multiplierOddNumber) {
        if (initialOddNumber % 2 == 0) {
            throw new IllegalArgumentException("HashCodeBuilder requires an odd initial value");
        }
        if (multiplierOddNumber % 2 == 0) {
            throw new IllegalArgumentException("HashCodeBuilder requires an odd multiplier");
        }
        iConstant = multiplierOddNumber;
        iTotal = initialOddNumber;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>boolean</code>.
     * </p>
     * <p>
     * This adds <code>1</code> when true, and <code>0</code> when false to the <code>hashCode</code>.
     * </p>
     * <p>
     * This is in contrast to the standard <code>java.lang.Boolean.hashCode</code> handling, which computes
     * a <code>hashCode</code> value of <code>1231</code> for <code>java.lang.Boolean</code> instances
     * that represent <code>true</code> or <code>1237</code> for <code>java.lang.Boolean</code> instances
     * that represent <code>false</code>.
     * </p>
     * <p>
     * This is in accordance with the <i>Effective Java</i> design.
     * </p>
     *
     * @param value             the boolean to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final boolean value) {
        iTotal = iTotal * iConstant + (value ? 0 : 1);
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>boolean</code> array.
     * </p>
     *
     * @param array             the array to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final boolean[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final boolean element : array) {
                append(element);
            }
        }
        return this;
    }

    // -------------------------------------------------------------------------

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>byte</code>.
     * </p>
     *
     * @param value             the byte to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final byte value) {
        iTotal = iTotal * iConstant + value;
        return this;
    }

    // -------------------------------------------------------------------------

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>byte</code> array.
     * </p>
     *
     * @param array             the array to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final byte[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final byte element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>char</code>.
     * </p>
     *
     * @param value             the char to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final char value) {
        iTotal = iTotal * iConstant + value;
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>char</code> array.
     * </p>
     *
     * @param array             the array to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final char[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final char element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>double</code>.
     * </p>
     *
     * @param value             the double to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final double value) {
        return append(Double.doubleToLongBits(value));
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>double</code> array.
     * </p>
     *
     * @param array             the array to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final double[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final double element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>float</code>.
     * </p>
     *
     * @param value             the float to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final float value) {
        iTotal = iTotal * iConstant + Float.floatToIntBits(value);
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>float</code> array.
     * </p>
     *
     * @param array             the array to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final float[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final float element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for an <code>int</code>.
     * </p>
     *
     * @param value             the int to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final int value) {
        iTotal = iTotal * iConstant + value;
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for an <code>int</code> array.
     * </p>
     *
     * @param array             the array to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final int[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final int element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>long</code>.
     * </p>
     *
     * @param value             the long to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    // NOTE: This method uses >> and not >>> as Effective Java and
    //       Long.hashCode do. Ideally we should switch to >>> at
    //       some stage. There are backwards compat issues, so
    //       that will have to wait for the time being. cf LANG-342.
    public HashCodeBuilder append(final long value) {
        iTotal = iTotal * iConstant + ((int) (value ^ (value >> 32)));
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>long</code> array.
     * </p>
     *
     * @param array             the array to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final long[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final long element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for an <code>Object</code>.
     * </p>
     *
     * @param object             the Object to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final Object object) {
        if (object == null) {
            iTotal = iTotal * iConstant;

        } else {
            if(object.getClass().isArray()) {
                // 'Switch' on type of array, to dispatch to the correct handler
                // This handles multi dimensional arrays
                if (object instanceof long[]) {
                    append((long[]) object);
                } else if (object instanceof int[]) {
                    append((int[]) object);
                } else if (object instanceof short[]) {
                    append((short[]) object);
                } else if (object instanceof char[]) {
                    append((char[]) object);
                } else if (object instanceof byte[]) {
                    append((byte[]) object);
                } else if (object instanceof double[]) {
                    append((double[]) object);
                } else if (object instanceof float[]) {
                    append((float[]) object);
                } else if (object instanceof boolean[]) {
                    append((boolean[]) object);
                } else {
                    // Not an array of primitives
                    append((Object[]) object);
                }
            } else {
                iTotal = iTotal * iConstant + object.hashCode();
            }
        }
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for an <code>Object</code> array.
     * </p>
     *
     * @param array             the array to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final Object[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final Object element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>short</code>.
     * </p>
     *
     * @param value             the short to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final short value) {
        iTotal = iTotal * iConstant + value;
        return this;
    }

    /**
     * <p>
     * Append a <code>hashCode</code> for a <code>short</code> array.
     * </p>
     *
     * @param array             the array to add to the <code>hashCode</code>
     * @return this hash code builder
     */
    public HashCodeBuilder append(final short[] array) {
        if (array == null) {
            iTotal = iTotal * iConstant;
        } else {
            for (final short element : array) {
                append(element);
            }
        }
        return this;
    }

    /**
     * <p>
     * Adds the result of super.hashCode() to this builder.
     * </p>
     *
     * @param superHashCode             the result of calling <code>super.hashCode()</code>
     * @return this HashCodeBuilder, used to chain calls.
     * @since 2.0
     */
    public HashCodeBuilder appendSuper(final int superHashCode) {
        iTotal = iTotal * iConstant + superHashCode;
        return this;
    }

    /**
     * <p>
     * Return the computed <code>hashCode</code>.
     * </p>
     *
     * @return  <code>hashCode</code> based on the fields appended
     */
    public int toHashCode() {
        return iTotal;
    }

    /**
     * Returns the computed <code>hashCode</code>.
     *
     * @return hashCode based on the fields appended
     */
    public int build() {
        return Integer.valueOf(toHashCode());
    }

    /**
     * The computed hashCode from toHashCode() is returned due to the likelihood
     * of bugs in mis-calling toHashCode() and the unlikeliness of it mattering what the hashCode for
     * HashCodeBuilder itself is.
     *
     * @return hashCode based on the fields appended
     */
    @Override
    public int hashCode() {
        return toHashCode();
    }

    /**
     * The type ID key.
     */
    static final class IDKey {
        private final Object value;
        private final int id;

        /**
         * Constructor for IDKey
         * @param _value The value
         */
        public IDKey(final Object _value) {
            // This is the Object hashcode
            id = System.identityHashCode(_value);
            // There have been some cases (LANG-459) that return the
            // same identity hash code for different objects.  So
            // the value is also added to disambiguate these cases.
            value = _value;
        }

        /**
         * returns hashcode - i.e. the system identity hashcode.
         * @return the hashcode
         */
        @Override
        public int hashCode() {
            return id;
        }

        /**
         * checks if instances are equal
         * @param other The other object to compare to
         * @return if the instances are for the same object
         */
        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof IDKey)) {
                return false;
            }
            final IDKey idKey = (IDKey) other;
            if (id != idKey.id) {
                return false;
            }
            // Note that identity equals is used.
            return value == idKey.value;
        }
    }

}
