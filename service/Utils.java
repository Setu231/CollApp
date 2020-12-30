
package io.collapp.service;

import java.util.ArrayList;
import java.util.List;

abstract class Utils {

	static List<Integer> filter(List<Integer> ids, List<Integer> toKeep) {
		List<Integer> r = new ArrayList<>();
		for (Integer id : ids) {
			if (toKeep.contains(id)) {
				r.add(id);
			}
		}
		return r;
	}
}
