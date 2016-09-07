/*
 * CAS - Cellular Automata Simulator
 * Copyright (C) 2016  Guilherme Humberto Jansen
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ghjansen.cas.core.physics;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.ghjansen.cas.core.ca.Combination;
import com.ghjansen.cas.core.ca.DimensionalCombination;
import com.ghjansen.cas.core.ca.DimensionalState;
import com.ghjansen.cas.core.ca.DimensionalTransition;
import com.ghjansen.cas.core.ca.State;
import com.ghjansen.cas.core.ca.Transition;
import com.ghjansen.cas.core.exception.InvalidAbsoluteTimeLimit;
import com.ghjansen.cas.core.exception.InvalidCombination;
import com.ghjansen.cas.core.exception.InvalidDimensionalAmount;
import com.ghjansen.cas.core.exception.InvalidDimensionalSpace;
import com.ghjansen.cas.core.exception.InvalidInitialCondition;
import com.ghjansen.cas.core.exception.InvalidRelativeTimeLimit;
import com.ghjansen.cas.core.exception.InvalidState;
import com.ghjansen.cas.core.exception.InvalidTransition;
import com.ghjansen.cas.core.exception.TimeLimitReached;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Guilherme Humberto Jansen (contact.ghjansen@gmail.com)
 */
public class SpaceTest {

	@Test
	public void dimensionalSpaceConstructor() throws CloneNotSupportedException, InvalidAbsoluteTimeLimit,
			InvalidRelativeTimeLimit, InvalidDimensionalAmount, InvalidInitialCondition, InvalidDimensionalSpace,
			InvalidState, InvalidTransition, InvalidCombination {
		final Time dimensionalTime = new DimensionalTime(1000, 1000);
		final Cell dimensionalCell = getNewValidDimensionalCell();
		final ArrayList<Cell> firstDimension = new ArrayList<Cell>();
		firstDimension.add(dimensionalCell);
		final Space dimensionalSpace = new DimensionalSpace(dimensionalTime, firstDimension, true);
		Assert.assertNotNull(dimensionalSpace.getCurrent());
		Assert.assertNotNull(dimensionalSpace.getHistory());
		Assert.assertTrue(dimensionalSpace.isKeepHistory());
	}

	@Test(expected = InvalidInitialCondition.class)
	public void dimensionalSpaceConstructorInvalidInitialCondition()
			throws CloneNotSupportedException, InvalidAbsoluteTimeLimit, InvalidRelativeTimeLimit,
			InvalidDimensionalAmount, InvalidInitialCondition, InvalidDimensionalSpace {
		final Time dimensionalTime = new DimensionalTime(1000, 1000);
		new DimensionalSpace(dimensionalTime, null, true);
	}

	@Test(expected = InvalidDimensionalSpace.class)
	public void dimensionalSpaceConstructorInvalidDimensionalSpace() throws CloneNotSupportedException,
			InvalidAbsoluteTimeLimit, InvalidRelativeTimeLimit, InvalidDimensionalAmount, InvalidInitialCondition,
			InvalidDimensionalSpace, InvalidState, InvalidTransition, InvalidCombination {
		final Time dimensionalTime = new DimensionalTime(1000, 1000);
		final Cell dimensionalCell = getNewValidDimensionalCell();
		final ArrayList<List> firstDimension = new ArrayList<List>();
		final ArrayList<Cell> secondDimension = new ArrayList<Cell>();
		firstDimension.add(secondDimension);
		secondDimension.add(dimensionalCell);
		new DimensionalSpace(dimensionalTime, firstDimension, true);
	}

	@Test(expected = InvalidDimensionalAmount.class)
	public void dimensionalSpaceConstructorInvalidDimensionalAmount() throws InvalidAbsoluteTimeLimit,
			InvalidDimensionalAmount, InvalidInitialCondition, InvalidDimensionalSpace {
		final Time dimensionalTime = new LimitedTime(1000);
		new DimensionalSpace(dimensionalTime, null, false);
	}

	@Test
	public void dimensionalGetCombination() throws CloneNotSupportedException, InvalidAbsoluteTimeLimit,
			InvalidRelativeTimeLimit, InvalidDimensionalAmount, InvalidInitialCondition, InvalidDimensionalSpace,
			TimeLimitReached, InvalidState, InvalidTransition, InvalidCombination {
		final Time dimensionalTime = new DimensionalTime(3, 1);
		final Cell dimensionalCell = getNewValidDimensionalCell();
		final ArrayList<Cell> firstDimension = new ArrayList<Cell>();
		firstDimension.add(dimensionalCell);
		final Space mockedDimensionalSpace = spy(new DimensionalSpace(dimensionalTime, firstDimension, true));
		mockedDimensionalSpace.getCombination(dimensionalTime);
		dimensionalTime.increase();
		mockedDimensionalSpace.getCombination(dimensionalTime);
		dimensionalTime.increase();
		mockedDimensionalSpace.getCombination(dimensionalTime);
		final ArgumentCaptor<Time> argumentTime = ArgumentCaptor.forClass(Time.class);
		final ArgumentCaptor<List> argumentList = ArgumentCaptor.forClass(List.class);
		verify(mockedDimensionalSpace, times(3)).getCombination(argumentTime.capture(), argumentList.capture());
		List expected = mockedDimensionalSpace.getInitial();
		assertEquals(expected, argumentList.getAllValues().get(0));
		expected = mockedDimensionalSpace.getLast();
		assertEquals(expected, argumentList.getAllValues().get(1));
		assertEquals(expected, argumentList.getAllValues().get(2));
	}

	@Test
	public void dimensionalSetState() throws CloneNotSupportedException, InvalidAbsoluteTimeLimit,
			InvalidRelativeTimeLimit, InvalidDimensionalAmount, InvalidInitialCondition, InvalidDimensionalSpace,
			TimeLimitReached, InvalidState, InvalidTransition, InvalidCombination {
		final Time dimensionalTime = new DimensionalTime(3, 2);
		final State dimensionalBlackState = new DimensionalState("black", 0);
		final State dimensionalWhiteState = new DimensionalState("white", 1);
		final Combination dimensionalCombination = new DimensionalCombination(dimensionalWhiteState,
				dimensionalBlackState, dimensionalBlackState);
		final Transition dimensionalTransition = new DimensionalTransition(dimensionalCombination,
				dimensionalBlackState);
		final Cell dimensionalCell = new DimensionalCell(dimensionalTransition);
		final ArrayList<Cell> firstDimension = new ArrayList<Cell>();
		firstDimension.add(dimensionalCell);
		final Space mockedDimensionalSpace = spy(new DimensionalSpace(dimensionalTime, firstDimension, true));
		mockedDimensionalSpace.setState(dimensionalTime, dimensionalTransition); // createNewCell
		dimensionalTime.increase(); // 0,1
		mockedDimensionalSpace.setState(dimensionalTime, dimensionalTransition); // createNewCell+rotate
		dimensionalTime.increase(); // 1,0
		mockedDimensionalSpace.setState(dimensionalTime, dimensionalTransition); // createNewIteration+Cell
		dimensionalTime.increase(); // 1,1
		mockedDimensionalSpace.setState(dimensionalTime, dimensionalTransition); // createNewCell+rotate
		dimensionalTime.increase(); // 2,0
		mockedDimensionalSpace.setState(dimensionalTime, dimensionalTransition); // createNewIteration+Cell
		dimensionalTime.increase(); // 2,1
		mockedDimensionalSpace.setState(dimensionalTime, dimensionalTransition); // createNewCell
		verify(mockedDimensionalSpace, times(2)).createNewIteration(dimensionalTime);
		verify(mockedDimensionalSpace, times(6)).createNewCell(dimensionalTime, dimensionalTransition);
	}

	private Cell getNewValidDimensionalCell() throws InvalidState, InvalidTransition, InvalidCombination {
		final State dimensionalBlackState = new DimensionalState("black", 0);
		final State dimensionalWhiteState = new DimensionalState("white", 1);
		final Combination dimensionalCombination = new DimensionalCombination(dimensionalWhiteState,
				dimensionalBlackState, dimensionalBlackState);
		final Transition dimensionalTransition = new DimensionalTransition(dimensionalCombination,
				dimensionalBlackState);
		return new DimensionalCell(dimensionalTransition);
	}

}
