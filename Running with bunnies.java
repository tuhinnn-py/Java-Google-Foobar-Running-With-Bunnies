import java.io.*;
public class Solution
{
	public static final int INF = 100000000;
	public static final int MEMO_SIZE = 10000;
	public static int[] lookup;
	public static int[] lookup_for_bunnies;
	
	public static int getHashValue(int[] state, int loc)
	{
		int hashval = 0;
		for(int i = 0; i < state.length; i++)
			hashval += state[i] * (1 << i);
		hashval += (1 << loc) * 100;

		return hashval % MEMO_SIZE;
	}
	
	public static boolean findNegativeCycle(int[][] times)
	{
		int i, j, k;
		int checkSum = 0;
		int V = times.length;
		
		int[][] graph = new int[V][V];
		for(i = 0; i < V; i++)
			for(j = 0; j < V; j++)
			{
				graph[i][j] = times[i][j];
				checkSum += times[i][j];
			}
		if(checkSum == 0)
			return true;
			
		for(k = 0; k < V; k++)
			for(i = 0; i < V; i++)
				for(j = 0; j < V; j++)
					if(graph[i][j] > graph[i][k] + graph[k][j])
						graph[i][j] = graph[i][k] + graph[k][j];
					
		for(i = 0; i < V; i++)
			if(graph[i][i] < 0)
				return true;
		return false;
	}
		
	public static void dfs(int[][] times, int[] state, int loc, int tm, int[] res)
	{
		int V = times.length;
		if(loc == V - 1)
		{
			int rescued = countArr(state);
			int maxRescued = countArr(res);
			
			if(maxRescued < rescued)
				for(int i = 0; i < V; i++)
					res[i] = state[i];
			
			if(rescued == V - 2)
			  return;
		}
		else if(loc > 0)
		  state[loc] = 1;
		
		int hashval = getHashValue(state, loc);
		if(tm < lookup[hashval])
			return;
		else if(tm == lookup[hashval] && countArr(state) <= lookup_for_bunnies[loc])
			return;
		else
		{
			lookup_for_bunnies[loc] = countArr(state);
			lookup[hashval] = tm;
			for(int i = 0; i < V; i++)
			{
				if(i != loc && (tm - times[loc][i]) >= 0)
				{
					boolean stateCache = state[i] == 1;
					dfs(times, state, i, tm - times[loc][i], res);
					if(stateCache)
						state[i] = 1;
					else
						state[i] = 0;
				}
			}
		}
	}
	
	public static int countArr(int[] arr)
	{
		int counter = 0;
		for(int i = 0; i < arr.length; i++)
		  if(arr[i] == 1)
		    counter++;
		return counter;
	}
	
	public static int bellmanFord(int[][] adj, int times_limit)
	{
		int V = adj.length;
		int i, j, k;
		int[][] graph = new int[V + 1][V + 1];
		
		for(i = 1; i <= V; i++)
		  graph[i][0] = INF;
		  
		for(i = 0; i < V; i++)
		  for(j = 0; j < V; j++)
		    graph[i + 1][j + 1] = adj[i][j];
		
		int[] distance = new int[V + 1] ;
		for(i = 1; i <= V; i++)
		  distance[i] = INF;
		  
		for(i = 1; i <= V; i++)
		  for(j = 0; j <= V; j++)
		  {
		  	int minDist = INF;
		  	for(k = 0; k <= V; k++)
		  	  if(graph[k][j] != INF)
		  	    minDist = Math.min(minDist, distance[k] + graph[k][j]);
		  	distance[j] = Math.min(distance[j], minDist);
		  }

		for(i = 0; i < V; i++)
		  for(j = 0; j < V; j++)
		    adj[i][j] += distance[i + 1] - distance[j + 1];

		return times_limit + distance[1] - distance[V];
	}
	
	public static int[] solution(int[][] times, int times_limit)
	{
		int V = times.length;
		if(V == 2)
		    return new int[]{};
		if(findNegativeCycle(times))
		{
			int ans[] = new int[times.length - 2];
			for(int i = 0; i < ans.length; i++)
			  ans[i] = i;
			return ans;
		}
		lookup = new int[MEMO_SIZE];
		lookup_for_bunnies = new int[V];
		for(int i = 0; i < V; i++)
			lookup_for_bunnies[i] = -1;
		
		times_limit = bellmanFord(times, times_limit);
		int initial[] = new int[V];
		int res[] = new int[V];
		
		dfs(times, initial, 0, times_limit, res);
		
		int len = countArr(res);
		int ans[] = new int[len];
		int counter = 0;
		for(int i = 0; i < res.length; i++)
		  if(res[i] == 1)
		  {
		    ans[counter++] = i - 1;
		    if(counter == len)
		      break;
		  }
		
		return ans;
	}
	
	public static void main(String args[])throws IOException
	{
		int[][] adj = new int[][]{{0, 2, 2, 2, -1}, {9, 0, 2, 2, -1}, {9, 3, 0, 2, -1}, {9, 3, 2, 0, -1}, {9, 3, 2, 2, 0}};
		int times_limit = 1;
		
		int[] ans = solution(adj, times_limit);
		for(int i : ans)
			System.out.print(i + " ");
	}
}
