Koennen wir hier ueber das Model sprechen ;-)



Initialisierung der PartitionPolicies:

 - in DatabaseSessionImpl wird #initializeDescriptors 2xmal aufgerufen, in der ersten Phase sind noch keine PartitionPolicies drin, erst in der 2
 - das Problem mit CustomPartitionPolicies ist, dass der Aufruf #initialize(AbstractSession ... ) nicht delegiert wird an das eigentliche delegate

 // Metadata processing
 getProject().addPartitioningPolicy(new RoundRobinPartitioningMetadata(annotation, this));
