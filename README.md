[![Build Status](https://travis-ci.com/hostettler/jdd.svg?branch=master)](https://travis-ci.com/hostettler/jdd)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=hostettler_jdd&metric=alert_status)](https://sonarcloud.io/dashboard?id=hostettler_jdd)
[![Coverage (Sonar)](https://sonarcloud.io/api/project_badges/measure?project=hostettler_jdd&metric=coverage)](https://sonarcloud.io/dashboard?id=hostettler_jdd)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# JDD - Java Decision Diagrams Library

This library provides Java support to encode Data Decision Diagrams [[1]](#1) and Hierarchical Set Decision Diagrams [[2]](#2) as well as set and homomorphic operations.


## How to integrate it in your project

For Maven users

```xml
<dependency>
  <groupId>net.hostettler.jdd</groupId>
  <artifactId>jdd</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

for Gradle users

```ruby
dependencies {
    implementation 'net.hostettler.jdd:jdd:1.0-SNAPSHOT'
}
```



## Data Decision Diagrams
Creating a DDD goes as follow :

```java
DD<String, Integer> dDD1 = DDDImpl.create("a", 1, DDDImpl.create("b", 2, DDDImpl.create("c", 3)));																		
```

or

```java
DD<String, Integer> dDD2 = DDDImpl.create("a", 1);
DD<String, Integer> dDD3 = DDDImpl.create("b", 2);
DD<String, Integer> dDD4 = DDDImpl.create("c", 3);
DD<String, Integer> dDD5 = dDD2.append(dDD3.append(dDD4));																	
```

Then doing set operations 

```java
DD<String, Integer> dDD6 = DDDImpl.create("a", 9, DDDImpl.create("b", 10, DDDImpl.create("c", 3)));
DD<String, Integer> dDD7 = dDD1.union(dDD6);
DD<String, Integer> dDD8 = dDD1.difference(dDD6);
DD<String, Integer> dDD9 = dDD1.intersection(dDD6);
```

Finally, writing homomorphisms

```java
Hom<String, Integer> filter = new SimplePropagationDDDHomImpl<String, Integer>() {
	protected DD<String, Integer> phi(String var, Integer val,
		Map<Integer, DD<String, Integer>> alpha, Object... parameters) {
			
		String str = (String) parameters[0];
		Integer integer = (Integer) parameters[1];
		
		if (var.equals(str) && val.equals(integer)) {
			return (DD) DDDImpl.DDD_FALSE;	
		} else {
			return DDDImpl.create(var, val, phi(id(alpha, val), parameters));	
		}
	}

};
	
filter.phi(dDD1, new Object[] { "c", 3 });
```


## Hierarchical Set Decision Diagrams

```java
DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a",  ObjSet.create(1,2,3), SDDImpl.create("b",  ObjSet.create(4,2,3)));
```
Set operations are recursive in the two dimensions but otherwise used in a similar way as for DDD.

Here is an example of homomorphism

```java
Hom<String, ValSet<Integer>> filter = new SimplePropagationSDDHomImpl<String, Integer>() {
	@Override
	protected DD<String, ValSet<Integer>> phi(String var, ValSet<Integer> values,
			Map<ValSet<Integer>, DD<String, ValSet<Integer>>> alpha, Object... parameters) {
		String str = (String) parameters[0];
		ValSet<Integer> valuesToFilterOut = (ValSet<Integer> ) parameters[1];
		
		return DDDImpl.create(var, values.difference(valuesToFilterOut), phi(id(alpha, values), parameters));	
	}
};
		
filter.phi(sDD1, new Object[] { "c", ObjSet.create(2,3) });
```

## Bibliography

<a id="1">[1]</a> 
Jean-Michel Couvreur, Emmanuelle Encrenaz, Emmanuel Paviot-Adet, Denis Poitrenaud, Pierre-André Wacrenier. Data Decision Diagrams for Petri Net analysis. 23th International Conference on Application and Theory of Petri Nets, Jun 2002, Adelaide, Australia. pp.101-120, 10.1007/3-540-48068-4_8 - https://hal.archives-ouvertes.fr/hal-01544997
<br/>
<a id="2">[2]</a> 
Alexandre Hamez, Yann Thierry-Mieg, Fabrice Kordon. Hierarchical Set Decision Diagrams and Automatic Saturation. 29th International Conference on Petri Nets and Other Models of Concurrency (ICATPN 2008), Jun 2008, Xian, China. pp.211-230,  10.1007/978-3-540-68746-7_16 - https://hal.archives-ouvertes.fr/hal-01303835

