package blogCode.easymk;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.fail;

import java.lang.reflect.Executable;

public class test {
    @Test
    public void whenUsingIsA_thenMatchesTypeAndRejectsNull() {
        Service mock = mock(Service.class);
        mock.process(isA(String.class));
        expectLastCall().times(1);
        replay(mock);

        mock.process("test");
        verify(mock);
    }
    @Test
    public void whenUsingIsAWithInheritance_thenMatchesSubclass() {
        Service mock = mock(Service.class);
        mock.handleRequest(isA(Request.class));
        expectLastCall().times(2);
        replay(mock);

        mock.handleRequest(new Request("normal"));
        mock.handleRequest(new SpecialRequest()); // SpecialRequest extends Request
        verify(mock);
    }
    @Test
    public void whenUsingIsAWithNull_thenFails() {
        Service mock = mock(Service.class);
        mock.process(isA(String.class));
        expectLastCall().times(1);
        replay(mock);

//        assertThrows(AssertionError.class, () -> {
//            mock.process(null);
//            verify(mock);
//        });
        exceptionRule.expect(AssertionError.class);
        mock.process(null);
        verify(mock);
    }
    @Test(expected = NullPointerException.class)
    public void whenExceptionThrown_thenExpectationSatisfied() {
        String test = null;
        test.length();
    }
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void whenExceptionThrown_thenRuleIsApplied() {
        exceptionRule.expect(NumberFormatException.class);
        exceptionRule.expectMessage("For input string");
        Integer.parseInt("1a");
    }
}
