/*
 * MIT License
 *
 * Copyright (c) 2021 Artyom Panfutov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package paqua.loan.amortization.api.impl.message;

/**
 * Messages for exceptions
 *
 * @author Artyom Panfutov
 */
public enum Messages {
    NULL("Input data can't be null!"),
    NEGATIVE_NUMBER("Input can't be negative!"),
    EARLY_PAYMENT_NUMBER_IS_NEGATIVE("Early payment number can't be negative!"),
    EARLY_PAYMENT_AMOUNT_IS_NEGATIVE("Early payment amount can't be negative!"),
    EARLY_PAYMENT_STRATEGY_IS_NULL("Early payment strategy can't be null");

    private final String messageText;

    public String getMessageText() {
        return messageText;
    }

    Messages(String messageText) {
        this.messageText = messageText;
    }
}
