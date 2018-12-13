
package jdz.farmKing.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BuyAmount {
	BUY_1, BUY_10, BUY_100, BUY_1000;
	@Getter private final int amount = Integer.parseInt(name().replace("BUY_", ""));
}
