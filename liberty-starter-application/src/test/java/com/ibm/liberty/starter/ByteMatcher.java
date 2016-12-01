package com.ibm.liberty.starter;

import org.hamcrest.*;

import java.nio.charset.StandardCharsets;

/**
 * Matcher to compare a byte array to a string. The byte array is assumed to be UTF-8 encoded.
 */
public class ByteMatcher extends BaseMatcher<byte[]> {

    private final Matcher<String> delegate;
    private final boolean removeSpaces;

    public ByteMatcher(String expectedValue, boolean removeSpaces) {
        this.delegate = Matchers.equalTo(removeWhiteSpace(expectedValue));
        this.removeSpaces = removeSpaces;
    }

    @Override
    public boolean matches(Object item) {
        if (item == null) {
            return delegate.matches(item);
        }
        if (item instanceof byte[]) {
            return delegate.matches(bytesAsString((byte[]) item));
        }
        return false;
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        if (item instanceof byte[]) {
            delegate.describeMismatch(bytesAsString((byte[]) item), description);
        } else {
            super.describeMismatch(item, description);
        }
    }

    private String bytesAsString(byte[] item) {
        String itemAsString = new String(item, StandardCharsets.UTF_8);
        itemAsString = removeWhiteSpace(itemAsString);
        return itemAsString;
    }

    private String removeWhiteSpace(String fromString) {
        if (removeSpaces) {
            return fromString.replaceAll("\\s", "");
        } else {
            return fromString;
        }
    }

    @Override
    public void describeTo(Description description) {
        delegate.describeTo(description);
    }

    /**
     * <p></p>Create a {@link Matcher} to compare a <code>byte[]</code> to a String.</p>
     * <p>Before matching any white space will be removed from both the byte array and the expected value.</p>
     */
    public static Matcher<byte[]> isByteArrayFor(String expectedValue) {
        return new ByteMatcher(expectedValue, true);
    }

    /**
     * <p></p>Create a {@link Matcher} to compare a <code>byte[]</code> to a String.</p>
     * <p>White space will be included in the comparison.</p>
     */
    public static Matcher<byte[]> isByteArrayIncludingSpacesFor(String expectedValue) {
        return new ByteMatcher(expectedValue, false);
    }
}
