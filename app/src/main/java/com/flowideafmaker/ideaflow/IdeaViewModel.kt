package com.flowideafmaker.ideaflow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class IdeaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = IdeaRepository(application)
    
    private val _ideas = MutableLiveData<List<Idea>>()
    val ideas: LiveData<List<Idea>> = _ideas

    private val _currentCategory = MutableLiveData<String>("All")
    val currentCategory: LiveData<String> = _currentCategory

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    val filteredIdeas: LiveData<List<Idea>> = androidx.lifecycle.MediatorLiveData<List<Idea>>().apply {
        addSource(_ideas) { ideas ->
            value = filterList(ideas, _currentCategory.value ?: "All", _searchQuery.value ?: "")
        }
        addSource(_currentCategory) { category ->
            value = filterList(_ideas.value ?: emptyList(), category, _searchQuery.value ?: "")
        }
        addSource(_searchQuery) { query ->
            value = filterList(_ideas.value ?: emptyList(), _currentCategory.value ?: "All", query)
        }
    }

    private fun saveToStorage(list: List<Idea>) {
        _ideas.value = list
        repository.saveIdeas(list)
    }

    private fun filterList(list: List<Idea>, category: String, query: String): List<Idea> {
        return list.filter { idea ->
            val matchesCategory = if (category == "All") true else idea.category.equals(category, ignoreCase = true)
            val matchesQuery = if (query.isEmpty()) true else (idea.title.contains(query, ignoreCase = true) || idea.description.contains(query, ignoreCase = true))
            matchesCategory && matchesQuery
        }
    }

    fun setCategory(category: String) {
        _currentCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    init {
        val savedIdeas = repository.loadIdeas()
        if (savedIdeas != null) {
            _ideas.value = savedIdeas
        } else {
            loadDummyData()
        }
    }

    private fun loadDummyData() {
        val dummyList = listOf(
            Idea(
                "1", "Quantum Neural Interface",
                "A high-bandwidth brain-computer interface for everyday mobile interaction.",
                "TECH", "Developing", "High", 40, "2 hours ago"
            ),
            Idea(
                "2", "Sustainable App Design",
                "Mobile app for carbon tracking and lifestyle optimization.",
                "BUSINESS", "Developing", "Medium", 65, "Yesterday"
            ),
            Idea(
                "3", "Spatial OS Architecture",
                "Operating system design for mixed reality environments.",
                "TECH", "Developing", "High", 15, "3 days ago"
            ),
            Idea(
                "4", "AI Writing Assistant",
                "Browser extension for content optimization using localized LLMs.",
                "TECH", "Developing", "Low", 88, "2 hours ago"
            ),
            Idea(
                "5", "Urban Farming Kits",
                "Automated hydroponic systems for city apartments.",
                "PERSONAL", "Completed", "Medium", 100, "1 week ago"
            ),
            Idea(
                "6", "Bio-degradable Packaging",
                "New material science approach to replace single-use plastics.",
                "BUSINESS", "Developing", "High", 30, "4 hours ago"
            ),
            Idea(
                "7", "Decentralized Energy Grid",
                "Peer-to-peer energy sharing platform for solar home owners.",
                "TECH", "Developing", "Medium", 10, "5 hours ago"
            ),
            Idea(
                "8", "Mindfulness VR",
                "Immersive environments designed specifically for deep focus and relaxation.",
                "PERSONAL", "Developing", "Low", 50, "6 hours ago"
            ),
            Idea(
                "9", "Smart Water Purifier",
                "IoT enabled water filter with real-time purity monitoring.",
                "TECH", "Developing", "Medium", 75, "7 hours ago"
            ),
            Idea(
                "10", "Eco-friendly Commute App",
                "Gamified navigation for finding the lowest carbon footprint paths.",
                "BUSINESS", "Developing", "High", 20, "8 hours ago"
            ),
            Idea(
                "11", "Legacy Project Alpha",
                "This is an older project that will be hidden if more than 10 exist.",
                "PERSONAL", "Completed", "Low", 100, "1 month ago"
            )
        )
        saveToStorage(dummyList)
    }

    fun updateProgress(id: String, newProgress: Int) {
        val currentList = _ideas.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val progress = newProgress.coerceIn(0, 100)
            val updatedIdea = currentList[index].copy(
                progress = progress,
                status = if (progress == 100) "Completed" else "Developing"
            )
            currentList[index] = updatedIdea
            saveToStorage(currentList)
        }
    }

    fun addIdea(idea: Idea) {
        val currentList = _ideas.value?.toMutableList() ?: mutableListOf()
        currentList.add(0, idea) // Add to top
        saveToStorage(currentList)
    }

    fun updateIdea(updatedIdea: Idea) {
        val currentList = _ideas.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == updatedIdea.id }
        if (index != -1) {
            currentList[index] = updatedIdea
            saveToStorage(currentList)
        }
    }

    fun deleteIdea(id: String) {
        val currentList = _ideas.value?.toMutableList() ?: return
        val removed = currentList.removeIf { it.id == id }
        if (removed) {
            saveToStorage(currentList)
        }
    }

    fun clearAllData() {
        saveToStorage(emptyList())
    }
}