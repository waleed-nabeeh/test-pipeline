# Extension Concept 

see also [Coding Guidelines](CodingGuidelineOnepager.md).

1. fork repo in client VCS for documentation and use this as dependency or the artifacts from mc artifactory
1. Use modules as is, :warning:  do not rename modules &  package names
1. Extension is preferred via additional client modules to keep code separated and allow an easier backport later
example checkout → mc3s-b2x-aggregator-checkout → <client>-b2x-aggregator-checkout
1. add dependency to required modules and extend / override b2x implementation as mentioned below

# Primary Bean

The developers can override existing beans using new beans with adding a Primary annotation

# Resolver
Resolvers can be used if more than one bean is available. In that case you can override the resolver, annotate this as Primary and implement a different lookup to get the required bean.

There are two resolver types available:

## NullableBaseResolver

They return the requested item, but could also return no result

```
@NotNull
Optional<T> resolve(@Nullable Object... args);
```

## BaseResolver

They return the requested item or a default result.


```
@NotNull
T resolve(@Nullable Object... args);
```

# Backport Approach

To enable backport and pull from accelerator:

1. Do not change package names 
1. Extend Components and Services, annotate as Primary
1. Keep Accelerator classes stable as long as possible 
1. Add Client specific modules

 